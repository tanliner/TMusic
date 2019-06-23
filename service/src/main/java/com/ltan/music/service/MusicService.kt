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
        fun updateLyric(title: String?, txt: String?)
        fun onPicUrl(url: String?)
    }

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mBinder: MyBinder

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        mBinder = MyBinder(this)
        mBinder.init(mediaPlayer)
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MusicLog.d(TAG, "onStartCommand intent: $intent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinder.stop()
        mediaPlayer.release()
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

        init {
            mUpdateThread.start()
            mCallbacks = ArrayList()
            mCurrentSong = SongPlaying(url = "")
            mPlayingList = ArrayList()
        }

        fun init(player: MediaPlayer) {
            this.mPlayer = player
            player.setOnBufferingUpdateListener { mp, percent ->
                mBufferPercent = percent
                onCallBackBuffer(percent)
                MusicLog.d(TAG, "buffer percent.... $mBufferPercent")
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
                mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
                mLyricsUpdater.sendMessage(mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC))
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

        private fun play(songUrl: String) {
            mPlayer.reset()
            mPlayer.setDataSource(songUrl)
            mPlayer.prepare()
            // start()
        }

        fun stop() {
            mPlayer.stop()
            mPlayer.reset()
            mUpdateThread.quitSafely()
            mCallbacks.clear()
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
                        mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
                        mLyricsUpdater.sendMessage(mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC))
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
            if (lyricPosition.nextDur == 0L && curPos > MSG_UPDATE_GAP) {
                return false
            }
            val msgDelay: Long =
                if (lyricPosition.nextDur >= MSG_UPDATE_GAP) lyricPosition.nextDur else lyricPosition.nextDur % MSG_UPDATE_GAP
            onCallBackUpdateLyric(mCurrentSong.title, lyricPosition.txt)

            val uMsg = mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC)
            mLyricsUpdater.sendMessageDelayed(uMsg, msgDelay)
            return true
        }

        private fun onCallBackBuffer(per: Int) {
            mCallbacks.forEach {
                it.onBufferUpdated(per)
            }
        }

        private fun onCallBackUpdateLyric(title: String?, lyricTxt: String?) {
            mCallbacks.forEach {
                it.updateLyric(title, lyricTxt)
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
            onCallBackPause()
            mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
        }

        override fun getBufferPercentage(): Int {
            return mBufferPercent
        }

        override fun seekTo(pos: Int) {
            mPlayer.seekTo(pos)
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
            mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
            mLyricsUpdater.sendEmptyMessage(MSG_UPDATE_LYRIC)
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