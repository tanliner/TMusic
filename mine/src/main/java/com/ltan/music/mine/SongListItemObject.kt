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
class SongListItemObject (
    val songId: Long,
    val imgId: Int,
    val title: String? = "",
    val count: Int = 0,
    var isHeartMode: Boolean = false
)