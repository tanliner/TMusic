package com.ltan.music.account.beans

/**
 * @describe :
 * @usage :
 *
 * </p>
 * Created by tanlin on 2019/5/17
 */
data class LoginResult(
    private val loginType: Int = 0,

    private val code: Int = 0,

    val account: Account? = null,

    val profile: Profile? = null,

    private val bindings: List<String>? = null
)