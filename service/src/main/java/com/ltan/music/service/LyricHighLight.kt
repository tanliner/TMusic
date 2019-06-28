package com.ltan.music.service

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
    fun onHighLight(txt: String?, index: Int = 0)
}