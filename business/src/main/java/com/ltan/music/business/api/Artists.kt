package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: Artists
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
data class Artists(
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
        return "Artists@${this.hashCode()}:{"+
                "name: $name\nid: $id\npicId: $picId\nimg1v1Id: $img1v1Id\nbriefDesc: $briefDesc\npicUrl: $picUrl" +
                "img1v1Url: $img1v1Url\nalbumSize: $albumSize\nalias: $alias\ntrans: $trans\nmusicSize: $musicSize" +
                "}"
    }
}