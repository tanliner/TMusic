package com.ltan.music.service.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager


/**
 * TMusic.com.ltan.music.service.widget
 *
 * @ClassName: CdViewPager
 * @Description:
 * @Author: tanlin
 * @Date:   2019-07-03
 * @Version: 1.0
 */
class CdViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    fun setDurationScroll(millis: Int) {
        try {
            val viewpager = ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller.set(this, OwnScroller(context, millis))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    class OwnScroller(context: Context, scrollFactor: Int) : Scroller(context, DecelerateInterpolator()) {

        private var mScrollFactor = 1

        init {
            this.mScrollFactor = scrollFactor
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, mScrollFactor * duration)
        }
    }
}