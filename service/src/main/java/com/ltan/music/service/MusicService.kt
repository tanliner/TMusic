package com.ltan.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.business.bean.SongUrl
import com.ltan.music.business.common.CommonApi
import com.ltan.music.common.LyricPosition
import com.ltan.music.common.LyricsObj
import com.ltan.music.common.LyricsUtil
import com.ltan.music.common.MusicLog
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.MusicController
import com.ltan.music.common.song.PlayMode
import com.ltan.music.common.song.ReqArgs
import com.ltan.music.service.api.MusicServiceApi
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * TMusic.com.ltan.music.service
 *
 * @ClassName: MusicService
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
class MusicService : Service() {
    companion object {
        const val TAG = "MusicService"
        const val ARGS_SONG_URL = "songUrl"
        const val MSG_UPDATE_LYRIC = 0x1001
        const val MSG_UPDATE_GAP = 1000L
    }

    /**
     * callback for view state update
     * eg: update the imageview from play to pause
     */
    interface IPlayerCallback {
        fun onStart()
        fun onPause()
        fun onCompleted(song: SongPlaying)
        fun onBufferUpdated(per: Int)
        /**
         * lyric ready
         */
        fun onLyricComplete(lyric: LyricsObj?)
        fun updateLyric(title: String?, txt: String?, index: Int = 0)
        fun updateTitle(title: String? = "", subtitle: String? = "", artist: String? = "")
        fun onPicUrl(url: String?)
    }

    private lateinit var mBinder: MyBinder

    override fun onBind(intent: Intent?): IBinder? {
        mBinder = MyBinder(this)
        mBinder.init(MediaPlayer())
        mBinder.checkThread()
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MusicLog.d(TAG, "onStartCommand intent: $intent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinder.stop()
    }

    class MyBinder(service: MusicService) : Binder(), MusicController {

        private var musicService: MusicService = service
        private lateinit var mPlayer: MediaPlayer

        private var mBufferPercent = 0
        // keep the one item
        private val mCurrentSong: SongPlaying
        private lateinit var mLyricsUpdater: Handler
        private var mUpdateThread: HandlerThread = HandlerThread("MusicService/MyBinder")
        private var mLyrics: LyricsObj? = null

        private val mCallbacks: ArrayList<IPlayerCallback>
        private var mPlayingList: ArrayList<SongItemObject>

        // for media player release, BLOCKED when player._reset or player._release in Native
        private val mMPReleaseThreads = ArrayList<Thread>()
        private var mReleaseThread: HandlerThread = HandlerThread("MusicService/MediaPlayerRelease")
        private lateinit var mReleaseThreadsWatcher: Handler

        private var mCurPlayIndex = 0

        init {
            mUpdateThread.start()
            mReleaseThread.start()
            mCallbacks = ArrayList()
            mCurrentSong = SongPlaying(url = "")
            mPlayingList = ArrayList()
        }

        fun init(player: MediaPlayer) {
            this.mPlayer = player
            player.setOnBufferingUpdateListener { mp, percent ->
                mBufferPercent = percent
                onCallBackBuffer(percent)
                MusicLog.v(TAG, "buffer percent.... $mBufferPercent")
            }
            player.setOnErrorListener { mp, what, extra ->
                // (-38,0) https://www.cnblogs.com/getherBlog/p/3939033.html
                MusicLog.e(TAG, "mp:$mp , init what: $what, extra:$extra")
                false
            }
            player.setOnPreparedListener {
                // (-38,0), already in onPrepared
                start()
            }
            player.setOnCompletionListener {
                onCallBackComplete(mCurrentSong)
                mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
            }
            mLyricsUpdater = Handler(mUpdateThread.looper, Handler.Callback { msg ->
                when (msg?.what) {
                    MSG_UPDATE_LYRIC -> {
                        updateCallbackLyric(mLyrics)
                    }
                    else -> {
                        false
                    }
                }
            })
            mReleaseThreadsWatcher = Handler(mReleaseThread.looper)
        }

        fun getService(): MusicService {
            return musicService
        }

        fun play(songId: Long) {
            // start()
        }

        fun addCallback(cb: IPlayerCallback) {
            mCallbacks.add(cb)
            if (isPlaying) {
                updateCallbackLyric(mLyrics)
            }
            cb.onPicUrl(mCurrentSong.picUrl)
        }

        fun removeCallback(cb: IPlayerCallback) {
            mCallbacks.remove(cb)
        }

        fun getCurrentSong(): SongPlaying {
            return mCurrentSong
        }

        fun setPlayingList(list: ArrayList<SongItemObject>) {
            mPlayingList.clear()
            mPlayingList.addAll(list)
        }

        fun getPlayingList(): ArrayList<SongItemObject> {
            return mPlayingList
        }

        fun play(song: SongPlaying) {
            play(song.url)
            queryLyric(song)
        }

        fun getLyric(): LyricsObj? {
            return mLyrics
        }

        /**
         * Get current index playing in the song-list
         */
        fun getCurrentIndex(): Int {
            return mCurPlayIndex
        }

        private fun play(songUrl: String) {
            if(isBuffering()) {
                internalStop()
                init(MediaPlayer())
            } else {
                // Just need reset
                mPlayer.reset()
            }
            mPlayer.setDataSource(songUrl)
            // asynchronously
            mPlayer.prepareAsync()
            // start()
        }

        private fun isBuffering(): Boolean {
            return mBufferPercent < 100
        }

        private fun internalStop() {
            val player = mPlayer
            val t = Thread(Runnable {
                player.stop()
                player.reset()
                player.release()
            })
            t.name = "Thread-$mBufferPercent%:" + System.currentTimeMillis() + ":" + mCurrentSong.title
            mMPReleaseThreads.add(t)
            t.start()
        }

        fun stop() {
            internalStop()
            mUpdateThread.quitSafely()
            mCallbacks.clear()
        }

        /**
         * because _reset the MediaPlayer will block Main-Thread, will use a sub thread to
         * stop & release MP.
         *
         * But, we want to know how long it is.
         * see [internalStop]
         */
        fun checkThread() {
            val iterator = mMPReleaseThreads.iterator()
            while (iterator.hasNext()) {
                val t = iterator.next()
                val state = t.state
                val alive = t.isAlive
                MusicLog.i(TAG, "watchThread : ${t.name}, state: $state, isAlive: $alive")
                if (state == Thread.State.TERMINATED || !alive) {
                    iterator.remove()
                }
            }
            mReleaseThreadsWatcher.postDelayed({ checkThread() }, 5000)
        }

        private fun queryLyric(song: SongPlaying) {
            mLyrics = null
            ApiProxy.instance.getApi(MusicServiceApi::class.java).getLyrics(song.id.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { rsp ->
                    if (rsp.lrc == null) {
                        LyricsObj()
                    }
                    LyricsUtil.parseLyricsInfo(rsp.lrc?.lyric)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(object : NormalSubscriber<LyricsObj>() {
                    override fun onNext(t: LyricsObj?) {
                        MusicLog.d(MusicService.TAG, "lyric of ${song.title}: $t")
                        mLyrics = t
                        onLyricComplete(t)
                        mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
                        updateCallbackLyric(t)
                    }
                })
        }

        /**
         * will send a delay msg {@link MusicService#MSG_UPDATE_LYRIC} like a loop, until next duration is 0
         * {@link queryLyric(int)}
         */
        private fun updateCallbackLyric(lyricsObj: LyricsObj?): Boolean {
            if (lyricsObj == null || lyricsObj.songTexts.isNullOrEmpty()) {
                return false
            }
            val curPos = currentPosition
            val lyricPosition = LyricsUtil.getCurrentSongLine(lyricsObj, curPos)
            if (curPos >= duration && curPos > MSG_UPDATE_GAP) {
                return false
            }
            val msgDelay: Long =
                if (lyricPosition.nextDur >= MSG_UPDATE_GAP) lyricPosition.nextDur else lyricPosition.nextDur % MSG_UPDATE_GAP
            onCallBackUpdateLyric(mCurrentSong.title, lyricPosition)

            val uMsg = mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC)
            mLyricsUpdater.sendMessageDelayed(uMsg, msgDelay)
            return true
        }

        private fun updateTitle(title: String? = "", subtitle: String? = "", artist: String? = "") {
            mCallbacks.forEach {
                it.updateTitle(title, subtitle, artist)
            }
        }

        private fun onLyricComplete(lyric: LyricsObj?) {
            mCallbacks.forEach {
                it.onLyricComplete(lyric)
            }
        }

        private fun onCallBackBuffer(per: Int) {
            mCallbacks.forEach {
                it.onBufferUpdated(per)
            }
        }

        private fun onCallBackUpdateLyric(title: String?, lyricPosition: LyricPosition) {
            mCallbacks.forEach {
                it.updateLyric(title, lyricPosition.txt, lyricPosition.index)
            }
        }

        private fun onCallBackComplete(curSong: SongPlaying) {
            mCallbacks.forEach {
                it.onCompleted(mCurrentSong)
            }
        }

        private fun onCallBackStart() {
            mCallbacks.forEach {
                it.onStart()
            }
        }

        private fun onCallBackPause() {
            mCallbacks.forEach {
                it.onPause()
            }
        }

        override fun isPlaying(): Boolean {
            return mPlayer.isPlaying
        }

        override fun canSeekForward(): Boolean {
            return true
        }

        override fun getDuration(): Int {
            return mPlayer.duration
        }

        override fun pause() {
            mPlayer.pause()
            mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
            onCallBackPause()
        }

        override fun getBufferPercentage(): Int {
            return mBufferPercent
        }

        override fun seekTo(pos: Int) {
            mPlayer.seekTo(pos)
            if(isPlaying) {
                updateCallbackLyric(mLyrics)
            }
        }

        override fun getCurrentPosition(): Int {
            return mPlayer.currentPosition
        }

        override fun canSeekBackward(): Boolean {
            return true
        }

        override fun start() {
            mPlayer.start()
            onCallBackStart()
            updateCallbackLyric(mLyrics)
        }

        override fun getAudioSessionId(): Int {
            return mPlayer.audioSessionId
        }

        override fun canPause(): Boolean {
            return true
        }

        override fun onNext() {
            MusicLog.d(TAG, "onNext init onNext click, curIndex: $mCurPlayIndex")
            if (mPlayingList.size > 0) {
                val next = mCurPlayIndex + 1 // circle
                mCurPlayIndex = next % mPlayingList.size
            }
            MusicLog.d(TAG, "onNext init onNext click, after curIndex: $mCurPlayIndex")
            val nextOne = mPlayingList[mCurPlayIndex]
            processNextSong(nextOne)
        }

        override fun onLast() {
            MusicLog.d(TAG, "onNext init onLast click, curIndex: $mCurPlayIndex")
            if (mPlayingList.size > 0) {
                val next = mCurPlayIndex - 1 // circle
                if (next < 0) {
                    // if last -1, select the last one
                    mCurPlayIndex = mPlayingList.size - 1
                }
                mCurPlayIndex = next % mPlayingList.size
            }
            MusicLog.d(TAG, "onNext init onLast click, after curIndex: $mCurPlayIndex")
            val nextOne = mPlayingList[mCurPlayIndex]
            processNextSong(nextOne)
        }

        private fun processNextSong(nextOne: SongItemObject) {
            mCurrentSong.id = nextOne.songId
            mCurrentSong.picUrl = nextOne.picUrl
            mCurrentSong.title = nextOne.title
            mCurrentSong.subtitle = nextOne.subTitle // TODO name roule
            mCurrentSong.url = nextOne.songUrl.toString()
            playNextSong(nextOne)
            // Mine/SongListActivity; Service/PlayerFragment
            updateTitle(nextOne.title, nextOne.subTitle, nextOne.artists)
        }

        override fun showList() {
        }

        override fun onModeChange(mode: PlayMode) {
        }

        private fun playNextSong(nextOne: SongItemObject) {
            // Assert: songUrl follow the picUrl
            if (nextOne.picUrl != null && nextOne.songUrl != null) {
                play(nextOne.songUrl!!)
                return
            }
            val LTAG = TAG
            val song = mCurrentSong
            val ids = ReqArgs.buildArgs(song.id)
            val controllers = ReqArgs.buildCollectors(song.id)
            ApiProxy.instance.getApi(CommonApi::class.java).getSongDetail(ids, controllers)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { rsp ->
                    val tracks = rsp.tracks
                    if (!tracks.isNullOrEmpty()) {
                        mCurrentSong.picUrl = tracks[0].al?.picUrl
                        for (track in tracks) {
                            if (track.id == mCurrentSong.id) {
                                // just update the picUrl
                                mCurrentSong.picUrl = track.al?.picUrl
                                MusicLog.d(LTAG, "onNext what a Rxjava2 picurl is:${track.al?.picUrl} ")
                                break
                            }
                        }
                    }
                    ApiProxy.instance.getApi(CommonApi::class.java).getSongUrl(ids)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap { t ->
                            Flowable.fromIterable(t.data)
                        }
                }
                .filter { it.id == mCurrentSong.id }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(object : NormalSubscriber<SongUrl>() {
                    override fun onNext(t: SongUrl?) {
                        MusicLog.d(LTAG, "onNext what a Rxjava2 songurl is$t\nurl:${t?.url}")
                        t?.url?.let {
                            mCurrentSong.url = it
                            play(mCurrentSong)
                        }
                    }
                })
        }
    }

    class MyAidlBinder : IMyAidlInterface.Stub() {
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
        }
    }
}