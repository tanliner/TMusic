package com.ltan.music.service.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.ltan.music.common.MusicLog
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.MusicController
import com.ltan.music.common.song.PlayMode
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
        private const val TAG = "PlayerPageController"
    }

    private var mPlayModeIv: ImageView
    private var mPlayLastIv: ImageView
    private var mPlayingIv: ImageView
    private var mPlayNextIv: ImageView
    private var mPlayListIv: ImageView
    private var mCurPosTv: TextView
    private var mSongDurationTv: TextView
    private var mSeekBar: SeekBar
    private var seekBarFromUsr = false

    private lateinit var mController: MusicController
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
        mCurPosTv = rootVIew.findViewById(R.id.tv_current_playing_position)
        mSongDurationTv = rootVIew.findViewById(R.id.tv_song_total_duration)
        mSeekBar = rootVIew.findViewById(R.id.seekbar_playing_song)
        init()
    }

    private fun init() {
        mPlayModeIv.setOnClickListener {
            mController.onModeChange(PlayMode.SINGLE)
        }
        mPlayLastIv.setOnClickListener {
            mController.onLast()
        }
        mPlayingIv.setOnClickListener {
            if(mController.isPlaying) {
                setState(false)
                mController.pause()
            } else {
                setState(true)
                mController.start()
            }
        }
        mPlayNextIv.setOnClickListener {
            mController.onNext()
        }
        mPlayListIv.setOnClickListener {
            MusicLog.d("init mPlayListIv click ${mDataSource.size}")
            mController.showList()
        }

        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                seekBarFromUsr = fromUser
                if (fromUser) {
                    mCurPosTv.text = processTimeStr(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekBarFromUsr = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBarFromUsr = false
                val progress = seekBar.progress
                if (progress > mController.currentPosition) {
                    MusicLog.w("onStopTrackingTouch progress not ready")
                }
                mController.seekTo(seekBar.progress)
            }
        })
    }

    /**
     * Update image for resume/pause
     */
    fun setState(isPlaying: Boolean) {
        if (isPlaying) {
            mPlayingIv.setImageResource(R.drawable.icon_player_page_pause)
        } else {
            mPlayingIv.setImageResource(R.drawable.icon_player_page_play)
        }
    }

    fun setMediaPlayer(controller: MusicController) {
        mController = controller
        mCurPosTv.text = processTimeStr(mController.currentPosition.toLong())
        mSongDurationTv.text = processTimeStr(mController.duration.toLong())
        mSeekBar.max = controller.duration
    }

    fun setDataSource(list: ArrayList<SongItemObject>) {
        mDataSource = list
    }

    fun updateMax(max: Int) {
        mSeekBar.max = max
        mSongDurationTv.text = processTimeStr(max.toLong())
    }

    fun updateCur(pos: Long) {
        if (!seekBarFromUsr) {
            mCurPosTv.text = processTimeStr(pos)
            mSeekBar.progress = pos.toInt()
        }
    }

    private fun processTimeStr(time: Long): String {
        val mil = time % 1000
        var sec = if (mil > 500) { time / 1000 + 1L } else {time / 1000}
        var min = if (sec % 60 >= 1) sec / 60 else (sec % 60)
        var hor = if (min % 60 >= 1) min / 60 else min % 60
        if (sec > 59) {
            sec %= 60
        }
        if (min > 59) {
            min %= 60
        }
        val minStr = if (min < 10) "0$min" else "$min"
        val secStr = if (sec < 10) "0$sec" else "$sec"
        return "$minStr:$secStr"
    }

    override fun onClick(v: View?) {
    }
}