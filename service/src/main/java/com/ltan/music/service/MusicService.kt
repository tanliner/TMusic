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
import com.ltan.music.common.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


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
        const val TAG = "playService"
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
        fun updateLyric(txt: String?)
    }

    interface ILyricsApi {
        @FormUrlEncoded
        @POST(ApiConstants.SONG_LYRICS)
        fun getLyrics(
            @Field("id") id: String,
            @Field(ApiConstants.CRYPTO_KEY) api: String = ApiConstants.CRYPTO_LINUX_API
            ): Flowable<LyricsRsp>
    }

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerControl: MediaController.MediaPlayerControl
    private lateinit var mBinder: MyBinder

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        // mediaPlayerControl = MediaController()
    }

    override fun onBind(intent: Intent?): IBinder? {
        mBinder = MyBinder(this)
        mBinder.init(mediaPlayer)
        // return MyBinder(this)
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MusicLog.d(TAG, "onStartCommand--- $intent")
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
        private var mUICallback: IPlayerCallback? = null
        private lateinit var mCurrentSong: SongPlaying
        private lateinit var mLyricsUpdater: Handler
        private var mUpdateThread: HandlerThread = HandlerThread("BinderHandlerThread")
        private var mLyrics: LyricsObj? = null
        init {
            mUpdateThread.start()
        }

        fun init(player: MediaPlayer) {
            this.mPlayer = player
            mCurrentSong = SongPlaying(url = "")
            player.setOnBufferingUpdateListener { mp, percent ->
                mBufferPercent = percent
                mUICallback?.onBufferUpdated(percent)
                MusicLog.d(TAG, "buffer percent.... $mBufferPercent")
            }
            player.setOnPreparedListener {
                start()
            }
            player.setOnCompletionListener {
                MusicLog.d(TAG, "play completed")
                mUICallback?.onCompleted(mCurrentSong)
                mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
            }
            mLyricsUpdater = Handler(mUpdateThread.looper, Handler.Callback { msg ->
                when(msg?.what) {
                    MSG_UPDATE_LYRIC -> {
                        val curPos = currentPosition
                        mLyrics?.let {
                            val lyricPosition = LyricsUtil.getCurrentSongLine(it, curPos)
                            if(lyricPosition.nextDur == 0L && curPos > MSG_UPDATE_GAP) {
                                return@let
                            }
                            val msgDelay: Long = if (lyricPosition.nextDur > MSG_UPDATE_GAP) lyricPosition.nextDur else lyricPosition.nextDur % MSG_UPDATE_GAP
                            MusicLog.i(TAG, "$lyricPosition, currentPos: $curPos, next time is: $msgDelay, callback is: $mUICallback")
                            mUICallback?.updateLyric(lyricPosition.txt)

                            val uMsg = mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC)
                            mLyricsUpdater.sendMessageDelayed(uMsg, msgDelay)
                        }
                        true
                    }
                    else -> { false }
                }
            })
        }

        fun getService(): MusicService {
            return musicService
        }

        fun play(songId: Long) {
            // start()
        }

        fun setCallback(cb: IPlayerCallback) {
            mUICallback = cb
            if (isPlaying) {
                mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
                mLyricsUpdater.sendMessage(mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC))
            }
        }

        fun getCurrentSong(): SongPlaying {
            return mCurrentSong
        }

        fun play(song: SongPlaying) {
            mCurrentSong = song
            play(song.url)
            ApiProxy.instance.getApi(ILyricsApi::class.java).getLyrics(song.id.toString())
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
                        MusicLog.d(TAG, " object song t: \t$t")
                        mLyrics = t
                        mLyricsUpdater.removeMessages(MSG_UPDATE_LYRIC)
                        mLyricsUpdater.sendMessage(mLyricsUpdater.obtainMessage(MSG_UPDATE_LYRIC))
                    }
                })

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
            mUICallback?.onPause()
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
            mUICallback?.onStart()
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