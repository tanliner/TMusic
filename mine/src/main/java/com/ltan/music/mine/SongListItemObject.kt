package com.ltan.music.mine

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongListItemObject
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-08
 * @Version: 1.0
 */
data class SongListItemObject (
    val songId: Long,
    val imgId: Int = 0,
    var imgUrl: String? = null,
    val title: String? = "",
    val count: Int = 0,
    var isHeartMode: Boolean = false
)