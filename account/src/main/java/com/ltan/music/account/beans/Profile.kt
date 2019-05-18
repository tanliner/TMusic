package com.ltan.music.account.beans

/**
 * @describe :
 * @usage :
 *
 * </p>
 * Created by tanlin on 2019/5/17
 */
data class Profile (
    private val description: String? = null,

    private val userType: Int = 0,

    private val mutual: Boolean = false,

    private val remarkName: String? = null,

    private val authStatus: Int = 0,

    private val djStatus: Int = 0,

    private val backgroundUrl: String? = null,

    private val avatarImgIdStr: Long = 0L,

    private val backgroundImgIdStr: String? = null,

    private val signature: String? = null,

    private val authority: Int = 0,

    private val userId: Long = 0,

    private val accountStatus: Int = 0,

    private val vipType: Int = 0,

    private val gender: Int = 0,

    private val nickname: String? = null,

    private val province: Int = 0,

    private val defaultAvatar: Boolean = false,

    private val avatarUrl: String? = null,

    private val birthday: Long = 0,

    private val city: Int = 0,

    private val detailDescription: String? = null,

    private val followed: Boolean = false,

    private val expertTags: String? = null,

    private val experts: Experts? = null,

    private val avatarImgId: Long = 0,

    private val backgroundImgId: Long = 0,

    private val avatarImgId_str: String? = null,

    private val followeds: Int = 0,

    private val follows: Int = 0,

    private val eventCount: Int = 0,

    private val playlistCount: Int = 0,

    private val playlistBeSubscribedCount: Int = 0
)