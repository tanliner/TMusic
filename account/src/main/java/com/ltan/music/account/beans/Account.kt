package com.ltan.music.account.beans

/**
 * TMusic.com.ltan.music.account
 * data-class
 *
 * @ClassName: Account
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-17
 * @Version: 1.0
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