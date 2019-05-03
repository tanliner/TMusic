package com.ltan.music.common

import android.content.Context

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: BaseApplication
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
object BaseApplication {
    private lateinit var mAppContext: Context
    private var isDebugMode = true
    private var sVersionName: String? = null

    fun initContext(base: Context) {
        mAppContext = base
        // sVersionName = AppUtil.getProjectVersionName(base)
    }

    fun getAPPContext(): Context {
        return mAppContext
    }
}