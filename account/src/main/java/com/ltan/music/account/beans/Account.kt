package com.ltan.music.account.beans

/**
 * @describe :
 * @usage :
 *
 * </p>
 * Created by tanlin on 2019/5/17
 */
data class Account(
    private val id: Long = 0,

    private val userName: String? = null,

    private val type: Int = 0,

    private val status: Int = 0,

    private val whitelistAuthority: Int = 0,

    private val createTime: Long = 0,

    private val salt: String? = null,

    private val tokenVersion: Int = 0,

    private val ban: Int = 0,

    private val baoyueVersion: Int = 0,

    private val donateVersion: Int = 0,

    private val vipType: Int = 0,

    private val viptypeVersion: Long = 0,

    private val anonimousUser: Boolean = false
)