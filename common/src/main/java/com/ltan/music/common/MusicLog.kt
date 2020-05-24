package com.ltan.music.common

import android.util.Log

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: MusicLog
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-01
 * @Version: 1.0
 */
object MusicLog {
    private const val TAG: String = "TMusic/"

    @JvmStatic
    private fun getTag(): String? {
        val trace =
            Throwable().fillInStackTrace().stackTrace
        var callingClass = ""
        for (i in 2 until trace.size) {
            val clazz: Class<*> = trace[i].javaClass
            if (clazz != MusicLog::class.java) {
                callingClass = trace[i].className
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1)
                break
            }
        }
        if (callingClass.contains("$")) {
            val end = callingClass.lastIndexOf("$")
            callingClass = callingClass.substring(0, end)
        }
        return "$callingClass: "
    }

    @JvmStatic
    fun v(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(this.TAG + TAG, msg)
        }
    }

    @JvmStatic
    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this.TAG, msg)
        }
    }

    @JvmStatic
    fun i(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(this.TAG + TAG, msg)
        }
    }

    @JvmStatic
    fun w(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(this.TAG + TAG, msg)
        }
    }

    @JvmStatic
    fun e(msg: String) {
        Log.e(this.TAG + getTag(), msg)
    }

    // -------------- log with throwable -------------- //
    @JvmStatic
    fun v(msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.v(this.TAG + TAG, msg, tr)
        }
    }
    @JvmStatic
    fun d(msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(this.TAG + TAG, msg, tr)
        }
    }

    @JvmStatic
    fun i(msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.i(this.TAG + TAG, msg, tr)
        }
    }

    @JvmStatic
    fun w(msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.w(this.TAG + TAG, msg, tr)
        }
    }

    @JvmStatic
    fun e(msg: String, tr: Throwable) {
        Log.e(this.TAG + TAG, msg, tr)
    }
}