package com.ltan.music.common

import android.os.Build

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: BuildVersion
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-01
 * @Version: 1.0
 */
object BuildVersion {

    @JvmStatic
    fun afterAndroidO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    @JvmStatic
    fun afterAndroidN(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    @JvmStatic
    fun afterAndroidM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @JvmStatic
    fun afterAndroidL(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    @JvmStatic
    fun afterAndroidK(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }
}