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

    val clientId: String = "",

    val effectTime: Long = 0,

    val code: Int = 0,

    val account: Account? = null,

    val token: String = "",

    val profile: Profile? = null,

    private val bindings: List<Binding>? = null
)

data class Binding(
    val userId: Long = 0,
    val url: String = "",
    val tokenJsonStr: String = "{}",
    val expiresIn: Long = 0,
    val refreshTime: Long = 0,
    val bindingTime: Long = 0,
    val expired: Boolean = false,
    val id: Long = 0,
    val type: Int = 0,
)
