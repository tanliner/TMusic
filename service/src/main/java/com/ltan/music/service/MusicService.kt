package com.ltan.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.MediaController
import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.common.ApiConstants
import com.ltan.music.common.LyricsRsp
import com.ltan.music.common.MusicLog
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
    }

    /**
     * callback for view state update
     * eg: update the imageview from play to pause
     */
    interface IPlayerCallback {
        fun onStart()
        fun onPause()
        fun onCompleted()
        fun onBufferUpdated(per: Int)
    }

    interface ILyricsApi {
        @FormUrlEncoded
        @POST(ApiConstants.SONG_LYRICS)
        fun getLyrics(
            @Field("id") id: Long
            ): Flowable<LyricsRsp>
    }

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerControl: MediaController.MediaPlayerControl

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        // mediaPlayerControl = MediaController()
    }

    override fun onBind(intent: Intent?): IBinder? {
        val binder = MyBinder(this)
        binder.init(mediaPlayer)
        // return MyBinder(this)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MusicLog.d(TAG, "onStartCommand--- $intent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    class MyBinder(service: MusicService) : Binder(), MediaController.MediaPlayerControl {

        private var musicService: MusicService = service
        private lateinit var player: MediaPlayer

        private var bufferPercent = 0
        private var callback: IPlayerCallback? = null
        private lateinit var mCurrentSong: SongPlaying

        fun init(player: MediaPlayer) {
            this.player = player
            player.setOnBufferingUpdateListener { mp, percent ->
                bufferPercent = percent
                callback?.onBufferUpdated(percent)
                MusicLog.d(TAG, "buffer percent.... $bufferPercent")
            }
            player.setOnPreparedListener {
                start()
            }
            player.setOnCompletionListener {
                MusicLog.d(TAG, "play completed")
                callback?.onCompleted()
            }
            mCurrentSong = SongPlaying(url = "")
        }

        fun getService(): MusicService {
            return musicService
        }

        fun play(songId: Long) {
            // start()
        }

        fun setCallback(cb: IPlayerCallback) {
            callback = cb
        }

        fun getCurrentSong(): SongPlaying {
            return mCurrentSong
        }

        fun play(song: SongPlaying) {
            mCurrentSong = song
            play(song.url)
            ApiProxy.instance.getApi(ILyricsApi::class.java).getLyrics(song.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(object : NormalSubscriber<LyricsRsp>() {
                    override fun onNext(t: LyricsRsp?) {
                        MusicLog.d(TAG, "t === $t")
                    }
                })

        }

        private fun play(songUrl: String) {
            player.reset()
            player.setDataSource(songUrl)
            player.prepare()
            // start()
        }

        override fun isPlaying(): Boolean {
            return player.isPlaying
        }

        override fun canSeekForward(): Boolean {
            return true
        }

        override fun getDuration(): Int {
            return player.duration
        }

        override fun pause() {
            player.pause()
            callback?.onPause()
        }

        override fun getBufferPercentage(): Int {
            return bufferPercent
        }

        override fun seekTo(pos: Int) {
            player.seekTo(pos)
        }

        override fun getCurrentPosition(): Int {
            return player.currentPosition
        }

        override fun canSeekBackward(): Boolean {
            return true
        }

        override fun start() {
            player.start()
            callback?.onStart()
        }

        override fun getAudioSessionId(): Int {
            return player.audioSessionId
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