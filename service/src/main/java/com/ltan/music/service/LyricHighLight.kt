package com.ltan.music.service

import com.ltan.music.common.LyricsObj

/**
 * TMusic.com.ltan.music.service
 *
 * @ClassName: LyricHighLight
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-28
 * @Version: 1.0
 */
interface LyricHighLight {
    fun onStart()
    fun onLyric(lyric: LyricsObj?)
    fun onHighLight(txt: String?, index: Int = 0)
}