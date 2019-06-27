package com.ltan.music.service.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.RelativeLayout
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.service.R

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: PlayerPageController
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-27
 * @Version: 1.0
 */
class PlayerPageController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {
    companion object {
        private const val TAG = "TMusic/PlayerPage"
    }

    private var mPlayModeIv: ImageView
    private var mPlayLastIv: ImageView
    private var mPlayingIv: ImageView
    private var mPlayNextIv: ImageView
    private var mPlayListIv: ImageView

    private lateinit var mController: MediaController.MediaPlayerControl
    private lateinit var mDataSource: ArrayList<SongItemObject>
    // current index of data list
    private var mCurPlayIndex = 0

    init {
        val rootVIew = LayoutInflater.from(context).inflate(R.layout.service_player_page_controler, this, true)
        mPlayModeIv = rootVIew.findViewById(R.id.iv_song_play_mode)
        mPlayLastIv = rootVIew.findViewById(R.id.iv_song_last_one)
        mPlayingIv = rootVIew.findViewById(R.id.iv_song_playing)
        mPlayNextIv = rootVIew.findViewById(R.id.iv_song_next_one)
        mPlayListIv = rootVIew.findViewById(R.id.iv_song_play_list)
        init()
    }

    private fun init() {
        mPlayModeIv.setOnClickListener {
            Log.d(TAG, "init mPlayModeIv click")
        }
        mPlayLastIv.setOnClickListener {
            Log.d(TAG, "init mPlayLastIv click")
            if (mDataSource.size > 0) {
                val next = mCurPlayIndex - 1
                if (next < 0) {
                    // if last -1, select the last one
                    mCurPlayIndex = mDataSource.size - 1
                }
                mCurPlayIndex = next % mDataSource.size
            }
        }
        mPlayingIv.setOnClickListener {
            Log.d(TAG, "init mPlayingIv click")
            if(mController.isPlaying) {
                setState(false)
                mController.pause()
            } else {
                setState(true)
                mController.start()
            }
        }
        mPlayNextIv.setOnClickListener {
            Log.d(TAG, "init mPlayNextIv click")
            if (mDataSource.size > 0) {
                val next = mCurPlayIndex + 1 // circle
                mCurPlayIndex = next % mDataSource.size
            }

        }
        mPlayListIv.setOnClickListener {
            Log.d(TAG, "init mPlayListIv click ${mDataSource.size}")
        }
    }

    fun setState(isPlaying: Boolean) {
        if (isPlaying) {
            mPlayingIv.setImageResource(R.drawable.icon_player_page_pause)
        } else {
            mPlayingIv.setImageResource(R.drawable.icon_player_page_play)
        }
    }

    fun setMediaPlayer(controller: MediaController.MediaPlayerControl) {
        mController = controller
    }

    fun setDataSource(list: ArrayList<SongItemObject>) {
        mDataSource = list
    }

    override fun onClick(v: View?) {
    }
}