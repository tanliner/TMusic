package com.ltan.music.mine.beans

import com.google.gson.annotations.SerializedName

/**
 * TMusic.com.ltan.music.mine.beans
 *
 * @ClassName: PlayList
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-18
 * @Version: 1.0
 */
data class PlayListRsp(
    // http resp
    val code: Int = 0,
    val more: Boolean = false,
    val playlist: List<PlayList>? = null
)

data class PlayList(
    val adType: Int = 0,

    val anonimous: Boolean = false,

    val artists: String? = null,

    val cloudTrackCount: Int = 0,

    val commentThreadId: String? = null,

    val coverImgId: Long = 0,

    val coverImgUrl: String? = null,

    val createTime: Long = 0,

    val creator: Creator? = null,

    val description: String? = null,

    val highQuality: Boolean = false,

    val id: Long = 0,

    val name: String? = null,

    val newImported: Boolean = false,

    val ordered: Boolean = false,

    val playCount: Int = 0,

    val privacy: Int = 0,

    val specialType: Int = 0,

    val status: Int = 0,

    val subscribed: Boolean = false,

    val subscribedCount: Int = 0,

    // Subscribers
    val subscribers: List<String>? = null,

    val tags: List<String>? = null,

    val totalDuration: Int = 0,

    val trackCount: Int = 0,

    val trackIds: List<TrackId>? = null,

    val trackNumberUpdateTime: Long = 0,

    val trackUpdateTime: Long = 0,

    val tracks: List<Tracks>? = null,

    val updateTime: Long = 0,

    val userId: Long = 0
)

data class Creator(
    val accountStatus: Int = 0,

    val authStatus: Int = 0,

    val authority: Int = 0,

    val avatarImgId: Long = 0,

    val avatarImgIdStr: String? = null,

    val avatarImgId_str: String? = null,

    val avatarUrl: String? = null,

    val backgroundImgId: Long = 0,

    val backgroundImgIdStr: String? = null,

    val backgroundUrl: String? = null,

    val birthday: Long = 0,

    val city: Int = 0,

    val defaultAvatar: Boolean = false,

    val description: String? = null,

    val detailDescription: String? = null,

    val djStatus: Int = 0,

    val expertTags: List<String>? = null,

    val experts: Experts? = null,

    val followed: Boolean = false,

    val gender: Int = 0,

    val mutual: Boolean = false,

    val nickname: String? = null,

    val province: Int = 0,

    val remarkName: String? = null,

    val signature: String? = null,

    val userId: Int = 0,

    val userType: Int = 0,

    val vipType: Int = 0
)

data class TrackId(
    val id: Long = 0L,
    @SerializedName("v")
    val v: Int
)

data class Tracks(
    val a: String? = null,

    val al: Al? = null,

    val alia: List<String>? = null,

    val ar: List<Ar>? = null,

    val cd: String? = null,

    val cf: String? = null,

    val copyright: Int = 0,

    val cp: Int = 0,

    val crbt: String? = null,

    val djId: Int = 0,

    val dt: Int = 0,

    val fee: Int = 0,

    val ftype: Int = 0,

    val h: H? = null,

    val id: Long = 0,

    val l: L? = null,

    val m: M? = null,

    val mst: Int = 0,

    val mv: Int = 0,

    val name: String? = null,

    val no: Int = 0,

    val pop: Int = 0,

    val pst: Int = 0,

    val publishTime: Long = 0,

    val rt: String? = null,

    val rtUrl: String? = null,

    val rtUrls: List<String>? = null,

    val rtype: Int = 0,

    val rurl: String? = null,

    @SerializedName("s_id")
    val sId: Int = 0,

    val st: Int = 0,

    val t: Int = 0,

    val v: Int = 0
)

data class Al(
    val id: Long = 0,

    val name: String? = null,

    val pic: Long = 0,

    val picUrl: String? = null,

    val tns: List<String>? = null
)

data class Ar (
    val alias: List<String>? = null,

    val id: Long = 0,

    val name: String? = null,

    val tns: List<String>? = null
)

data class H (
    val br: Long = 0,

    val fid: Int = 0,

    val size: Long = 0,

    val vd: Double = 0.toDouble()
)

data class M (
    val br: Long = 0,

    val fid: Int = 0,

    val size: Long = 0,

    val vd: Double = 0.toDouble()
)

data class L (
    val br: Long = 0,

    val fid: Int = 0,

    val size: Long = 0,

    val vd: Double = 0.toDouble()
)

data class Privileges (
    val cp: Int = 0,

    val cs: Boolean = false,

    val dl: Int = 0,

    val fee: Int = 0,

    val fl: Int = 0,

    val flag: Int = 0,

    val id: Int = 0,

    val maxbr: Int = 0,

    val payed: Int = 0,

    val pl: Int = 0,

    val preSell: Boolean = false,

    val sp: Int = 0,

    val st: Int = 0,

    val subp: Int = 0,

    val toast: Boolean = false
)

class Subscribers {}

class Experts {
    @SerializedName("2")
    val section: String? = null
}