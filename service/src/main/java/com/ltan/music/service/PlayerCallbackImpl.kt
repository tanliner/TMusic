package com.ltan.music.service

import com.ltan.music.common.LyricsObj
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
    private lateinit var lyricHighlight: LyricHighLight

    fun setLyricHighLight(highLight: LyricHighLight) {
        this.lyricHighlight = highLight
    }

    override fun onStart() {
        controller.setState(true)
        lyricHighlight.onStart()
    }

    override fun onPause() {
        controller.setState(false)
        lyricHighlight.onPause()
    }

    override fun onCompleted(song: SongPlaying) {
        controller.setState(false)
    }

    override fun onBufferUpdated(per: Int) {
        MusicLog.v(TAG, "music source buffered $per")
    }

    override fun onLyricComplete(lyric: LyricsObj?) {
        lyricHighlight.onLyric(lyric)
    }

    /**
     * call on sub thread
     */
    override fun updateLyric(title: String?, txt: String?, index: Int) {
        if (txt.isNullOrEmpty()) {
            return
        }
        lyricHighlight.onHighLight(txt, index)
    }

    override fun onPicUrl(url: String?) {
    }
}