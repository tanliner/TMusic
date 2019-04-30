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
    fun v(TAG: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(this.TAG + TAG, msg)
        }
    }

    @JvmStatic
    fun d(TAG: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(this.TAG + TAG, msg)
        }
    }

    @JvmStatic
    fun i(TAG: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg)
        }
    }

    @JvmStatic
    fun w(TAG: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, msg)
        }
    }

    @JvmStatic
    fun e(TAG: String, msg: String) {
        Log.e(TAG, msg)
    }

    // -------------- log with throwable -------------- //
    @JvmStatic
    fun v(TAG: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.v(this.TAG + TAG, msg, tr)
        }
    }
    @JvmStatic
    fun d(TAG: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg, tr)
        }
    }

    @JvmStatic
    fun i(TAG: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg, tr)
        }
    }

    @JvmStatic
    fun w(TAG: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, msg, tr)
        }
    }

    @JvmStatic
    fun e(TAG: String, msg: String, tr: Throwable) {
        Log.e(TAG, msg, tr)
    }
}