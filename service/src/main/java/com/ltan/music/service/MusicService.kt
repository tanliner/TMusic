package com.ltan.music.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.MediaController
import com.ltan.music.common.MusicLog

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

        fun play(songUrl: String) {
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