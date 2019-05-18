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
data class PlayListRsp (
    // http resp
    val code: Int = 0,
    val more: Boolean = false,
    val playlist: List<PlayList>? = null
)

data class PlayList(

    val subscribers: List<Subscribers>? = null,

    val subscribed: Boolean = false,

    val creator: Creator? = null,

    val artists: String? = null,

    val tracks: String? = null,

    val tags: List<String>? = null,

    val subscribedCount: Int = 0,

    val cloudTrackCount: Int = 0,

    val ordered: Boolean = false,

    val status: Int = 0,

    val adType: Int = 0,

    val highQuality: Boolean = false,

    val createTime: Long = 0,

    val trackNumberUpdateTime: Long = 0,

    val description: String? = null,

    val specialType: Int = 0,

    val privacy: Int = 0,

    val updateTime: Long = 0,

    val trackUpdateTime: Long = 0,

    val trackCount: Int = 0,

    val commentThreadId: String? = null,

    val coverImgUrl: String? = null,

    val coverImgId: Long = 0,

    val playCount: Int = 0,

    val newImported: Boolean = false,

    val userId: Long = 0,

    val anonimous: Boolean = false,

    val totalDuration: Int = 0,

    val name: String? = null,

    val id: Long = 0
)

data class Creator (
    val defaultAvatar: Boolean = false,

    val province: Int = 0,

    val authStatus: Int = 0,

    val followed: Boolean = false,

    val avatarUrl: String? = null,

    val accountStatus: Int = 0,

    val gender: Int = 0,

    val city: Int = 0,

    val birthday: Long = 0,

    val userId: Int = 0,

    val userType: Int = 0,

    val nickname: String? = null,

    val signature: String? = null,

    val description: String? = null,

    val detailDescription: String? = null,

    val avatarImgId: Long = 0,

    val backgroundImgId: Long = 0,

    val backgroundUrl: String? = null,

    val authority: Int = 0,

    val mutual: Boolean = false,

    val expertTags: List<String>? = null,

    val experts: Experts? = null,

    val djStatus: Int = 0,

    val vipType: Int = 0,

    val remarkName: String? = null,

    val backgroundImgIdStr: String? = null,

    val avatarImgIdStr: String? = null,

    val avatarImgId_str: String? = null
)

class Subscribers {}

class Experts {
    @SerializedName("2")
    val section: String? = null
}