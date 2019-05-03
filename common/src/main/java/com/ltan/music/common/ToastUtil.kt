package com.ltan.music.common

import android.widget.Toast
import androidx.annotation.NonNull
import com.ltan.music.common.ToastUtil.makeToast

/**
 * TMusic.com.ltan.music.common
 *
 * maybe a multi thread problem of this [makeToast] method
 *
 * @ClassName: ToastUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
object ToastUtil {

    @Volatile
    private var mToast: Toast? = null

    fun showToastShort(@NonNull text: String) {
        custom(text, Toast.LENGTH_SHORT)?.show()
    }

    fun showToastLong(@NonNull text: CharSequence) {
        custom(text, Toast.LENGTH_LONG)?.show()
    }

    fun custom(@NonNull message: CharSequence, duration: Int): Toast? {
        return makeToast(message, duration)
    }

    @Synchronized
    private fun makeToast(@NonNull message: CharSequence, duration: Int): Toast? {
        val context = BaseApplication.getAPPContext()
        if (mToast == null) {
            mToast = Toast.makeText(context, message, duration)
        } else {
            mToast?.setText(message)
            mToast?.duration = duration
        }
        return mToast
    }

    /**
     * 这里reset是在UI基类里做的，并不是所有的Activity都继承了UI.但是mToast本身占用内存很小，暂时不处理。
     * 这里的context为全局，不会出现内存泄露情况
     */
    fun resetToast() {
        if (mToast != null) {
            mToast = null
        }
    }
}