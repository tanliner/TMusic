package com.ltan.music

import android.content.Intent
import android.os.Bundle
import com.ltan.music.account.utils.AccountUtil
import com.ltan.music.basemvp.BaseActivity
import com.ltan.music.common.MusicLog

/**
 * TMusic.com.ltan.music
 *
 * @ClassName: RouterActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-18
 * @Version: 1.0
 */
class RouterActivity : BaseActivity() {

    override fun contentLayout(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AccountUtil.init()
        if(hasLoginInfo()) {
            startActivity(Intent(baseContext, MainActivity::class.java))
        } else {
            startActivity(Intent(baseContext, LoginActivity::class.java))
        }
        finish()
    }

    private fun hasLoginInfo(): Boolean {
        val account = AccountUtil.getAccountInfo()
        val profile = AccountUtil.getProfileInfo()
        MusicLog.d("account is: $account\tprofile is: $profile")
        return account != null && profile != null
    }
}