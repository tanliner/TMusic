package com.ltan.music.business.common

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.ltan.music.common.BaseApplication

/**
 * TMusic.com.ltan.music.business.common
 *
 * @ClassName: CookieUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-09-16
 * @Version: 1.0
 */
object CookieUtil {
    @JvmStatic
    fun clearCookies() {
        val appCtx = BaseApplication.getAPPContext()
        val ss = SharedPrefsCookiePersistor(appCtx)
        ss.clear()
    }
}