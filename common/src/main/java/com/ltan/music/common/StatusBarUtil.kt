package com.ltan.music.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.IntRange






/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: StatusBarUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-01
 * @Version: 1.0
 */
object StatusBarUtil {
    private const val DEFAULT_STATUS_BAR_ALPHA = 0xFF / 2
    private val FAKE_STATUS_BAR_VIEW_ID = R.id.common_fake_status_bar_view
    private val FAKE_TRANSLUCENT_VIEW_ID = R.id.common_translucent_view
    private const val TAG_KEY_HAVE_SET_OFFSET = -123

    @JvmStatic
    fun setColor(activity: Activity, @ColorInt color: Int) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA)
    }

    @JvmStatic
    fun setColor(activity: Activity, @ColorInt color: Int, @IntRange(from = 0, to = 255) statusBarAlpha: Int) {
        if (BuildVersion.afterAndroidL()) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = calculateStatusColor(color, statusBarAlpha)
        } else if (BuildVersion.afterAndroidK()) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val decorView = activity.window.decorView as ViewGroup
            val fakeStatusBarView = decorView.findViewById<View>(FAKE_STATUS_BAR_VIEW_ID)
            if (fakeStatusBarView == null) {
                decorView.addView(createStatusBarView(activity, color, statusBarAlpha))
            } else {
                if (fakeStatusBarView.visibility == View.GONE) {
                    fakeStatusBarView.visibility = View.VISIBLE
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha))
            }
            setRootView(activity)
        }
    }

    /**
     * Between Android K and L
     * create a statusBar
     * [activity] [color] [alpha]
     *
     */
    @JvmStatic
    private fun createStatusBarView(activity: Activity, @ColorInt color: Int, alpha: Int): View {
        val statusBarView = View(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha))
        statusBarView.id = FAKE_STATUS_BAR_VIEW_ID
        return statusBarView
    }

    @JvmStatic
    private fun setRootView(activity: Activity) {
        val parent = activity.findViewById<View>(android.R.id.content) as ViewGroup
        val count = parent.childCount
        var i = 0
        while (i < count) {
            val childView = parent.getChildAt(i)
            if (childView is ViewGroup) {
                childView.setFitsSystemWindows(true)
                childView.clipToPadding = true
            }
            i++
        }
    }

    @JvmStatic
    private fun getStatusBarHeight(context: Context): Int {
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resId)
    }

    @JvmStatic
    private fun calculateStatusColor(@ColorInt color: Int, alpha: Int): Int {
        if (alpha == 0) {
            return color
        }
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

}
