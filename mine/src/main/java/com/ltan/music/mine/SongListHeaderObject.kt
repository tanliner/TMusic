package com.ltan.music.mine

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongListHeaderObject
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-01
 * @Version: 1.0
 */
data class SongListHeaderObject(
    var previewUrl: String?,
    var title: String?,
    var subtitle: String?,
    var owner: String?,
    var extra: String = "",
    var songSize: Int = 0
) {
    constructor() : this(previewUrl = "", title = "", subtitle = "", owner = "")
}