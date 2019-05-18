package com.ltan.music.account.beans

/**
 * TMusic.com.ltan.music.account
 * data-class
 *
 * API: /weapi/login
 * API2: /weapi/v1/user/detail/{uid}
 *
 * @ClassName: Profile
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-17
 * @Version: 1.0
 */
data class Profile(
    private val accountStatus: Int = 0,
    private val authStatus: Int = 0,
    private val authority: Int = 0,
    private val avatarImgId: Long = 0,
    private val avatarImgIdStr: String? = null,
    private val avatarImgId_str: String? = null,
    private val avatarUrl: String? = null,

    private val backgroundImgId: Long = 0,
    private val backgroundImgIdStr: String? = null,
    private val backgroundUrl: String? = null,
    private val birthday: Long = 0,
    private val blacklist: Boolean = false,

    private val cCount: Int = 0,
    private val city: Int = 0,

    private val defaultAvatar: Boolean = false,
    private val description: String? = null,
    private val detailDescription: String? = null,
    private val djStatus: Int = 0,

    private val eventCount: Int = 0,
    private val expertTags: String? = null,
    private val experts: Experts? = null,

    private val followed: Boolean = false,
    private val followeds: Int = 0,
    private val follows: Int = 0,

    private val gender: Int = 0,

    private val mutual: Boolean = false,

    private val nickname: String? = null,

    private val playlistBeSubscribedCount: Int = 0,
    private val playlistCount: Int = 0,
    private val province: Int = 0,

    private val remarkName: String? = null,

    private val sCount: Int = 0,
    private val sDJPCount: Int = 0,
    private val signature: String? = null,

    private val userId: Long = 0,
    private val userType: Int = 0,

    private val vipType: Int = 0,

    val userPoint: UserPoint? = null
)

data class UserPoint(
    private val balance: Int = 0,
    private val blockBalance: Int = 0,

    private val status: Int = 0,

    private val updateTime: Long = 0L,

    private val userId: Long = 0,
    private val version: Int = 0
)