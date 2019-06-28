package com.ltan.music.mine

import com.bumptech.glide.Glide
import com.ltan.music.common.MusicLog
import com.ltan.music.service.MusicService
import com.ltan.music.service.SongPlaying
import com.ltan.music.widget.MusicPlayerController

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: PlayerCallbackImpl
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-21
 * @Version: 1.0
 */
class PlayerCallbackImpl(control: MusicPlayerController) : MusicService.IPlayerCallback {
    companion object {
        const val TAG = "PlayerCallbackImpl"
    }

    private val controller = control
    override fun onStart() {
        controller.setState(true)
    }

    override fun onPause() {
        controller.setState(false)
    }

    override fun onCompleted(song: SongPlaying) {
        controller.setState(false)
        controller.updateDisplay(song.title, song.subtitle)
    }

    override fun onBufferUpdated(per: Int) {
        MusicLog.v(TAG, "music source buffered $per")
    }

    /**
     * call on sub thread
     */
    override fun updateLyric(title: String?, txt: String?, index: Int) {
        if (txt.isNullOrEmpty()) {
            return
        }
        if (title != controller.getTitle()) {
            controller.post { controller.updateTitle(title) }
        }
        controller.post { controller.updateSummary(txt) }
    }

    override fun onPicUrl(url: String?) {
        Glide.with(controller.context)
            .load(url)
            .into(controller.mPreviewIv)
    }
}