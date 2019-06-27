package com.ltan.music.service

import com.ltan.music.common.MusicLog
import com.ltan.music.service.widget.PlayerPageController

/**
 * TMusic.com.ltan.music.service
 *
 * @ClassName: PlayerCallbackImpl
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-28
 * @Version: 1.0
 */
class PlayerCallbackImpl(control: PlayerPageController) : MusicService.IPlayerCallback {
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
    }

    override fun onBufferUpdated(per: Int) {
        MusicLog.v(TAG, "music source buffered $per")
    }

    /**
     * call on sub thread
     */
    override fun updateLyric(title: String?, txt: String?) {
        if(txt.isNullOrEmpty()) {
            return
        }
    }

    override fun onPicUrl(url: String?) {
    }
}