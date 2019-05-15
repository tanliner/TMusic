package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: Artist
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
data class Artist(
    var name: String? = null,

    var id: Int = 0,

    var picId: Int = 0,

    var img1v1Id: Int = 0,

    var briefDesc: String? = null,

    var picUrl: String? = null,

    var img1v1Url: String? = null,

    var albumSize: Int = 0,

    var alias: List<String>? = null,

    var trans: String? = null,

    var musicSize: Int = 0
) {
    override fun toString(): String {
        return "Artist:{" +
                "name: $name\nid: $id\npicId: $picId\nimg1v1Id: $img1v1Id\nbriefDesc: $briefDesc\n" +
                "picUrl: $picUrl\nimg1v1Url: $img1v1Url\nalbumSize: $albumSize\nalias: $alias\n" +
                "trans: $trans\nmusicSize: $musicSize" +
                "}"
    }
}