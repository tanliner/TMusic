package com.ltan.music.service

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.*
import android.view.animation.LinearInterpolator
import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.business.bean.SongUrl
import com.ltan.music.business.bean.Track
import com.ltan.music.business.common.CommonApi
import com.ltan.music.common.*
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.MusicController
import com.ltan.music.common.song.PlayMode
import com.ltan.music.common.song.SongUtils
import com.ltan.music.service.api.MusicServiceApi
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap

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
        const val CONS_THREAD_CHECK_GAP = 5000L
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

        /**
         * Update new lyric line
         * [curSong] of current song, [txt] lyric line, [index] of all lyrics
         */
        fun updateLyric(curSong: SongPlaying, txt: String?, index: Int = 0)

        /**
         * Usually to update the bottom MediaController view.
         */
        fun updateTitle(title: String? = "", subtitle: String? = "", artist: String? = "")

        /**
         * called when picture of current song ready
         */
        fun onSongPicUpdated(url: String?)
        /**
         * the next song find out, notify callbacks
         * [index] the new song index
         * [curSong] the new song, the lyric maybe not ready, please check the [onLyricComplete]
         */
        fun onNext(index: Int, curSong: SongPlaying)

        /**
         * the last song find out, notify callbacks
         * [index] the new song index
         * [curSong] the new song, the lyric maybe not ready, please check the [onLyricComplete]
         */
        fun onLast(index: Int, curSong: SongPlaying)
    }

    private lateinit var mBinder: MyBinder
    private lateinit var mMediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        mMediaSession = MediaSession(this, "music-session-tag")
        // MusicLog.e("a", "wtf ---> + $mMediaSession")
        // mMediaSession.setCallback(SessionCallback(this, mBinder))
    }

    override fun onBind(intent: Intent?): IBinder? {
        mBinder = MyBinder(this)
        mBinder.init(MediaPlayer())
        mBinder.checkThread()
        return mBinder
    }

    private fun updatePlaybackState(isPlaying: Boolean) {
        val stateBuilder = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY
                    or PlaybackState.ACTION_PLAY_PAUSE
                    or PlaybackState.ACTION_PLAY_FROM_MEDIA_ID
                    or PlaybackState.ACTION_PAUSE
                    or PlaybackState.ACTION_SKIP_TO_NEXT
                    or PlaybackState.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackState.CONTENTS_FILE_DESCRIPTOR.toLong()
            )
        if (isPlaying) {
            stateBuilder.setState(
                PlaybackState.STATE_PLAYING,
                PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                SystemClock.elapsedRealtime().toFloat()
            )
        } else {
            stateBuilder.setState(
                PlaybackState.STATE_PAUSED,
                PlaybackState.PLAYBACK_POSITION_UNKNOWN,
                SystemClock.elapsedRealtime().toFloat()
            )
        }
        mMediaSession.setPlaybackState(stateBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MusicLog.d("onStartCommand intent: $intent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinder.stop()
        sessionRelease()
    }

    private fun sessionRelease() {
        mMediaSession.setCallback(null)
        mMediaSession.isActive = false
        mMediaSession.release()
    }

    class SessionCallback(service: MusicService, controller: MusicController) : MediaSession.Callback() {
        private val mController: MusicController = controller
        private val mService: MusicService = service

        override fun onCommand(command: String, args: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, args, cb)
        }

        override fun onPause() {
            mController.pause()
            // mService.updatePlaybackState(false)
        }

        override fun onPlay() {
            mController.start()
            // mService.updatePlaybackState(true)
        }

        override fun onSkipToPrevious() {
            mController.onLast()
        }

        override fun onSkipToNext() {
            mController.onNext()
        }
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
        private val unavailableSongs = ConcurrentHashMap<Long, SongItemObject>()
        private val mServiceApiUtil = ApiUtil()
        // For prevent (-38, 0)
        private var mDuration = 0

        init {
            mUpdateThread.start()
            mReleaseThread.start()
            mCallbacks = ArrayList()
            mCurrentSong = SongPlaying(url = "")
            mPlayingList = ArrayList()
            mServiceApiUtil.setCallBack(object : ApiUtil.ApiCallback {
                override fun onLyrics(lyric: LyricsObj?) {
                    setLyrics(lyric)
                    updateCurrentLyric(lyric)
                }

                override fun onPicUrl(url: String?) {
                    setPicUrl(url)
                }

                override fun onSongDetail() {
                }

                override fun onSongUrl(url: String?, nextOne: SongItemObject, backward: Boolean) {
                    setSongUrl(url, nextOne)
                    if (url == null && backward) {
                        putUnavailableSong(nextOne)
                        onLast()
                    } else if (url == null) {
                        putUnavailableSong(nextOne)
                        onNext()
                    } else {
                        play(mCurrentSong)
                    }
                }
            })
        }

        fun init(player: MediaPlayer) {
            this.mPlayer = player
            player.setOnBufferingUpdateListener { mp, percent ->
                mBufferPercent = percent
                onCallBackBuffer(percent)
                MusicLog.v("buffer percent.... $mBufferPercent")
            }
            player.setOnErrorListener { mp, what, extra ->
                // (-38,0) https://www.cnblogs.com/getherBlog/p/3939033.html
                MusicLog.e("mp:$mp , init what: $what, extra:$extra")
                // prevent completed
                true
            }
            player.setOnPreparedListener {
                // (-38,0), already in onPrepared
                mDuration = it.duration
                start()
            }
            player.setOnCompletionListener {
                onCallBackComplete(mCurrentSong)
                mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
                // TODO, play mode
                onNext()
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
            cb.onSongPicUpdated(mCurrentSong.picUrl)
            cb.updateTitle(mCurrentSong.title, mCurrentSong.subtitle)
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

        /**
         * Prepare play a new song, It's necessary to query lyrics if empty.
         */
        fun play(song: SongPlaying) {
            // reset to 0
            mDuration = 0
            play(song.url)
            if (!song.lyrics?.songTexts.isNullOrEmpty()) {
                setLyrics(song.lyrics)
                updateCurrentLyric(song.lyrics)
            } else {
                // queryLyric(song)
                mServiceApiUtil.getLyric(song)
            }
            musicService.mMediaSession.isActive = true
            // if (!musicService.mMediaSession.isActive) {
            // }
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

        /**
         * index update by swipe view-page
         */
        fun setCurrentIndex(index: Int) {
            mCurPlayIndex = index
        }

        /**
         * play a online song [songUrl]
         *
         * Note: ANR if buffering a FLAC music while switch a new song.
         * so, create a new MediaPlayer to handle the new song, a sub-thread to watch the FLAC music
         */
        private fun play(songUrl: String) {
            if (isBuffering()) {
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
                MusicLog.i( "watchThread : ${t.name}, state: $state, isAlive: $alive")
                if (state == Thread.State.TERMINATED || !alive) {
                    iterator.remove()
                }
            }
            mReleaseThreadsWatcher.postDelayed({ checkThread() }, CONS_THREAD_CHECK_GAP)
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
            onCallBackUpdateLyric(mCurrentSong, lyricPosition)

            // todo: SKIP if no callback exist
            mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
            val uMsg = mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC)
            mLyricsUpdater.sendMessageDelayed(uMsg, msgDelay)
            return true
        }

        private fun updateTitle(title: String? = "", subtitle: String? = "", artist: String? = "") {
            mCallbacks.forEach { it.updateTitle(title, subtitle, artist) }
        }

        private fun updateSongPic(url: String?) {
            mCallbacks.forEach { it.onSongPicUpdated(url) }
        }

        private fun onLyricComplete(lyric: LyricsObj?) {
            mCallbacks.forEach { it.onLyricComplete(lyric) }
        }

        private fun onCallBackBuffer(per: Int) {
            mCallbacks.forEach { it.onBufferUpdated(per) }
        }

        private fun onCallBackUpdateLyric(curSong: SongPlaying, lyricPosition: LyricPosition) {
            mCallbacks.forEach { it.updateLyric(curSong, lyricPosition.txt, lyricPosition.index) }
        }

        private fun onCallBackComplete(curSong: SongPlaying) {
            mCallbacks.forEach { it.onCompleted(mCurrentSong) }
        }

        private fun onCallBackStart() {
            mCallbacks.forEach { it.onStart() }
        }

        private fun onCallBackPause() {
            mCallbacks.forEach { it.onPause() }
        }

        private fun onCallBackNext() {
            mCallbacks.forEach { it.onNext(mCurPlayIndex, mCurrentSong) }
        }

        private fun onCallBackLast() {
            mCallbacks.forEach { it.onLast(mCurPlayIndex, mCurrentSong) }
        }

        override fun isPlaying(): Boolean {
            return mPlayer.isPlaying
        }

        override fun canSeekForward(): Boolean {
            return true
        }

        override fun getDuration(): Int {
            // note: do not use player.duration directly, lead to wrong state (-38, 0)
            return mDuration
        }

        override fun pause() {
            mPlayer.volumeGradient(1.0F, 0F, { mPlayer.pause() })
            // mPlayer.pause()
            mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
            onCallBackPause()
        }

        override fun getBufferPercentage(): Int {
            return mBufferPercent
        }

        override fun seekTo(pos: Int) {
            mPlayer.seekTo(pos)
            if (isPlaying) {
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
            mPlayer.volumeGradient(0.0F, 1.0F)
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
            mPlayer.pause()
            mCurPlayIndex = SongUtils.getNextIndex(mCurPlayIndex, mPlayingList.size)
            val nextOne = mPlayingList[mCurPlayIndex]
            if (processNextSong(nextOne, false)) {
                onCallBackNext()
            }
        }

        override fun onLast() {
            mPlayer.pause()
            mCurPlayIndex = SongUtils.getLastIndex(mCurPlayIndex, mPlayingList.size)
            val nextOne = mPlayingList[mCurPlayIndex]
            if(processNextSong(nextOne, true)) {
                onCallBackLast()
            }
        }

        override fun showList() {
        }

        override fun onModeChange(mode: PlayMode) {
        }

        private fun processNextSong(nextOne: SongItemObject, backward: Boolean): Boolean {
            if (handleUnavailableSong(nextOne, backward)) {
                return false
            }
            mCurrentSong.id = nextOne.songId
            mCurrentSong.picUrl = nextOne.picUrl
            mCurrentSong.title = nextOne.title
            mCurrentSong.subtitle = nextOne.subTitle // TODO name rule
            mCurrentSong.url = nextOne.songUrl.toString()
            mCurrentSong.lyrics = nextOne.lyrics
            playNextSong(nextOne, backward)
            return true
        }

        private fun playNextSong(nextOne: SongItemObject, backward: Boolean = false) {
            if (nextOne.picUrl != null && nextOne.songUrl != null) {
                play(mCurrentSong)
                updateTitle(nextOne.title, nextOne.subTitle, nextOne.artists)
                updateSongPic(nextOne.picUrl)
                return
            }
            mServiceApiUtil.getSongDetail(mCurrentSong)
            mServiceApiUtil.getSongUrl(mCurrentSong, nextOne, backward)
        }

        private fun setSongUrl(url: String?, nextOne: SongItemObject) {
            mPlayingList[mCurPlayIndex].songUrl = url
            url?.let {
                mCurrentSong.url = it
                // Mine/SongListActivity; Service/PlayerFragment
                updateTitle(nextOne.title, nextOne.subTitle, nextOne.artists)
            }
        }

        private fun setPicUrl(url: String?) {
            mPlayingList[mCurPlayIndex].picUrl = url
            mCurrentSong.picUrl = url
            updateSongPic(url)
        }

        /**
         * update the lyric for the current playing song,
         * and save for the play-list, reduce the NetWork Request
         */
        private fun setLyrics(lyrics: LyricsObj?) {
            mPlayingList[mCurPlayIndex].lyrics = lyrics
            mCurrentSong.lyrics = lyrics
            mLyrics = lyrics
        }

        /**
         * update lyric to client with a background loop
         */
        private fun updateCurrentLyric(lyric: LyricsObj?) {
            onLyricComplete(lyric)
            mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
            updateCallbackLyric(lyric)
        }

        /**
         * the song url is empty, unless a VIP user
         */
        private fun putUnavailableSong(item: SongItemObject) {
            unavailableSongs[item.songId] = item
        }

        /**
         * may be a vip song-url
         */
        private fun unavailableSong(song: SongItemObject): Boolean {
            return unavailableSongs.containsKey(song.songId)
        }

        /**
         * [nextOne] may not be free version, song url is empty.
         * so, find the next Song via [backward]
         */
        private fun handleUnavailableSong(nextOne: SongItemObject, backward: Boolean): Boolean {
            val unavailable = unavailableSong(nextOne)
            var handled = false
            if (unavailable && backward) {
                onLast()
                handled = true
            } else if (unavailable) {
                onNext()
                handled = true
            }
            return handled
        }

        /**
         * volume growUp/dropDown
         */
        private fun MediaPlayer.volumeGradient(from: Float, to: Float,
                                               doneCallback: (() -> Unit)? = null) {
            val animator = ValueAnimator.ofFloat(from, to)
            animator.duration = 600
            animator.interpolator = LinearInterpolator()
            animator.addUpdateListener {
                val volume = it.animatedValue as Float
                try {
                    // prevent exception if state change
                    setVolume(volume, volume)
                } catch (e: Exception) {
                    it.cancel()
                }
            }
            animator.addListener(object : SimpleAnimator() {
                override fun onAnimationCancel(animation: Animator?) {
                    try {
                        setVolume(from, from)
                    } catch (ignore: Exception) {}
                }

                override fun onAnimationEnd(animation: Animator?) {
                    try {
                        setVolume(to, to)
                    } catch (ignore: Exception) {}
                    doneCallback?.invoke()
                }
            })
            animator.start()
        }
    }

    /**
     * for service http request
     *
     * ApiProxy.instance.getApi().getData().subscribe()
     */
    class ApiUtil {
        companion object {
            private const val TAG = "ApiUtil"
        }

        interface ApiCallback {
            /** request song lyric */
            fun onLyrics(lyric: LyricsObj?)
            fun onPicUrl(url: String?)
            /** request song detail, just use a picurl now */
            fun onSongDetail()
            /** request song url, xxx.mp3 */
            fun onSongUrl(url: String?, nextOne: SongItemObject, backward: Boolean)
        }

        private var mCallBackImpl: ApiCallback? = null

        fun setCallBack(cb: ApiCallback) {
            mCallBackImpl = cb
        }

        fun getLyric(song: SongPlaying) {
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
                        MusicLog.d("lyric of ${song.title}: $t")
                        mCallBackImpl?.onLyrics(t)
                    }
                })
        }

        fun getSongDetail(song: SongPlaying) {

            val ids = SongUtils.buildArgs(song.id)
            val controllers = SongUtils.buildCollectors(song.id)

            ApiProxy.instance.getApi(CommonApi::class.java).getSongDetail(ids, controllers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.tracks }
                .safeSubscribe(object : NormalSubscriber<List<Track>>() {
                    override fun onNext(t: List<Track>?) {
                        val tracks = t ?: return
                        for (track in tracks) {
                            if (track.id == song.id) {
                                // just update the picUrl
                                mCallBackImpl?.onPicUrl(track.al?.picUrl)
                                MusicLog.d("onNext what a Rxjava2 picurl is:${track.al?.picUrl} ")
                                break
                            }
                        }
                    }
                })
        }

        fun getSongUrl(song: SongPlaying, nextOne: SongItemObject, backward: Boolean) {
            val ids = SongUtils.buildArgs(song.id)
            ApiProxy.instance.getApi(CommonApi::class.java).getSongUrl(ids)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { Flowable.fromIterable(it.data) }
                .filter { it.id == song.id }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(object : NormalSubscriber<SongUrl>() {
                    override fun onNext(t: SongUrl?) {
                        MusicLog.d("onNext what a Rxjava2 songurl ${song.title} is$t\nurl:${t?.url}")
                        t ?: return
                        mCallBackImpl?.onSongUrl(t.url, nextOne, backward)
                    }
                })
        }

        /**
         * for test
         *
         * Deprecated
         */
        private fun testFlatMap(song: SongPlaying, nextOne: SongItemObject) {
            val ids = SongUtils.buildArgs(song.id)
            val controllers = SongUtils.buildCollectors(song.id)
            ApiProxy.instance.getApi(CommonApi::class.java).getSongDetail(ids, controllers)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { rsp ->
                    val tracks = rsp.tracks
                    if (!tracks.isNullOrEmpty()) {
                        song.picUrl = tracks[0].al?.picUrl
                        for (track in tracks) {
                            if (track.id == song.id) {
                                mCallBackImpl?.onPicUrl(track.al?.picUrl)
                                MusicLog.d("onNext what a Rxjava2 picurl is:${track.al?.picUrl} ")
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
                .filter { it.id == song.id }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(object : NormalSubscriber<SongUrl>() {
                    override fun onNext(t: SongUrl?) {
                        MusicLog.d("onNext what a Rxjava2 songurl ${song.title} is$t\nurl:${t?.url}")
                        val reqSongUrl = t ?: return
                        mCallBackImpl?.onSongUrl(t.url, nextOne, backward = false)
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