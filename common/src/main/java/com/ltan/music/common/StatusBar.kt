package com.ltan.music.common

import android.content.Context

/**
 * TMusic.com.ltan.music.common
 *
 * Please use the library: https://github.com/laobie/StatusBarUtil
 * version: 1.5.1
 *
 * @ClassName: StatusBar
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-01
 * @Version: 1.0
 */
object StatusBar {
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resId)
    }
}
