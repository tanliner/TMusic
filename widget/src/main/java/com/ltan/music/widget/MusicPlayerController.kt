package com.ltan.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: MusicPlayerController
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-20
 * @Version: 1.0
 */
class MusicPlayerController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle), View.OnClickListener {

    private var mPlayer: MediaController.MediaPlayerControl? = null

    private val mPreviewIv: ImageView
    private val mPlayIv: ImageView
    private val mFavoriteIv: ImageView
    private val mTitleTv: TextView
    private val mSummaryTv: TextView

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.media_controller, this, true)
        mPreviewIv = findViewById(R.id.iv_player_controller_preview)
        mPlayIv = findViewById(R.id.iv_player_controller_play)
        mFavoriteIv = findViewById(R.id.iv_player_controller_favorite)
        mTitleTv = findViewById(R.id.tv_player_title)
        mSummaryTv = findViewById(R.id.tv_player_summary)
        setOnClickListener(this)
        mFavoriteIv.setOnClickListener(this)
        mPlayIv.setOnClickListener(this)
        setBackgroundColor(resources.getColor(R.color.color_play_controller_bg))
    }

    override fun onClick(v: View?) {
        when (v) {
            mPlayIv -> onPlayClick()
            mFavoriteIv -> onFavorite()
        }
    }

    fun setPlayer(controller: MediaController.MediaPlayerControl) {
        mPlayer = controller
        setState(controller.isPlaying)
    }

    private fun onPlayClick() {
        if(mPlayer == null) {
            return
        }
        if (mPlayer!!.isPlaying) {
            onPaulse()
        } else {
            onPlay()
        }
        setState(mPlayer!!.isPlaying)
    }

    fun updateViewState() {
        if(mPlayer == null) {
            return
        }
        setState(mPlayer!!.isPlaying)
    }

    fun updateDisplay(title: String?, summary: String?) {
        mTitleTv.text = title
        mSummaryTv.text = summary
    }

    fun updateTitle(title: String?) {
        mTitleTv.text = title
    }

    fun updateSummary(summary: String?) {
        mSummaryTv.text = summary
    }

    /**
     * Simply to switch the play/pause image
     * [isPlaying] indicate whether media-player is playing or not
     */
    fun setState(isPlaying: Boolean) {
        if (isPlaying) {
            mPlayIv.setImageResource(R.drawable.note_btn_pause_white)
        } else {
            mPlayIv.setImageResource(R.drawable.note_btn_play_white)
        }
    }

    private fun onPlay() {
        mPlayer?.start()
    }

    private fun onPaulse() {
        mPlayer?.pause()
    }

    fun onFavorite() {

    }

}