package com.ltan.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.widget.MediaController
import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.common.LyricPosition
import com.ltan.music.common.LyricsObj
import com.ltan.music.common.LyricsUtil
import com.ltan.music.common.MusicLog
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.service.api.MusicServiceApi
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

    class MyBinder(service: MusicService) : Binder(), MediaController.MediaPlayerControl {

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
        private lateinit var mReleaseThreadsWacher: Handler

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
                MusicLog.e(TAG, "mp:$mp , init what: $what, extra:$extra")
                false
            }
            player.setOnPreparedListener {
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
            mReleaseThreadsWacher = Handler(mReleaseThread.looper)
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
            mReleaseThreadsWacher.postDelayed({ checkThread() }, 5000)
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