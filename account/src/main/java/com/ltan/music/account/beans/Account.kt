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
    val id: Long = 0,

    val userName: String? = null,

    val type: Int = 0,

    val status: Int = 0,

    val whitelistAuthority: Int = 0,

    val createTime: Long = 0,

    val salt: String? = null,

    val tokenVersion: Int = 0,

    val ban: Int = 0,

    val baoyueVersion: Int = 0,

    val donateVersion: Int = 0,

    val vipType: Int = 0,

    val viptypeVersion: Long = 0,

    val anonimousUser: Boolean = false
)