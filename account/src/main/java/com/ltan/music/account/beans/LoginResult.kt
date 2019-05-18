package com.ltan.music.account.beans

/**
 * TMusic.com.ltan.music.account
 * data-class
 *
 * @ClassName: LoginResult
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-17
 * @Version: 1.0
 */
data class LoginResult(
    val loginType: Int = 0,

    val code: Int = 0,

    val account: Account? = null,

    val profile: Profile? = null,

    private val bindings: List<String>? = null
)