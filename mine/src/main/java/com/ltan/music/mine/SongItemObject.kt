package com.ltan.music.mine

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongItemObject
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
data class SongItemObject(
    var number: Int = 0,
    var title: String? = null,
    var songId: Long = 0,
    var subTitle: String? = null,
    var video: Boolean = false,
    var songUrl: String? = null
)