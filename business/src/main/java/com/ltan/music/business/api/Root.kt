package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: Root
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
data class Root(
    var name: String,

    var id: Int = 0,

    var position: Int = 0,

    var alias: List<String>? = null,

    var status: Int = 0,

    var fee: Int = 0,

    var copyrightId: Int = 0,

    var disc: String? = null,

    var no: Int = 0,

    var artists: List<Artists>? = null,

    var album: Album? = null,

    var starred: Boolean = false,

    var popularity: Int = 0,

    var score: Int = 0,

    var starredNum: Int = 0,

    var duration: Int = 0,

    var playedNum: Int = 0,

    var dayPlays: Int = 0,

    var hearTime: Int = 0,

    var ringtone: String? = null,

    var crbt: String? = null,

    var audition: String? = null,

    var copyFrom: String? = null,

    var commentThreadId: String? = null,

    var rtUrl: String? = null,

    var ftype: Int = 0,

    var rtUrls: List<RtUrls>? = null,

    var copyright: Int = 0,

    var transName: String? = null,

    var sign: String? = null,

    var bMusic: BMusic? = null,

    var mp3Url: String? = null,

    var rtype: Int = 0,

    var rurl: String? = null,

    var mvid: Int = 0,

    var hMusic: HMusic? = null,

    var mMusic: MMusic? = null,

    var lMusic: LMusic? = null,

    var alg: String? = null
)
{
    override fun toString(): String {
        return super.toString() + name
    }
}
