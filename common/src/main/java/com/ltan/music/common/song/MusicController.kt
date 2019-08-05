package com.ltan.music.common.song

import android.widget.MediaController

/**
 * TMusic.com.ltan.music.common.song
 * for next button, last button and playing mode
 *
 * @ClassName: MusicController
 * @Description:
 * @Author: tanlin
 * @Date:   2019-07-01
 * @Version: 1.0
 */
interface MusicController : MediaController.MediaPlayerControl {
    fun onNext()
    fun onLast()
    fun showList()
    fun onModeChange(mode: PlayMode)
}