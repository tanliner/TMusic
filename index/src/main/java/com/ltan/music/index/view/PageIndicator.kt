package com.ltan.music.index.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager

/**
 * TMusic.com.ltan.music.index.view
 *
 * @ClassName: PageIndicator
 * @Description:
 * @Author: tanlin
 * @Date:   2019/4/8
 * @Version: 1.0
 */
class PageIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        val TAG = "ltan/PageIndicator"
    }

    private val mContext: Context = context

    // like a constructor in java
    init {
    }

    private lateinit var mViewPager: ViewPager
    fun setViewPager(pager: ViewPager) {
        mViewPager = pager
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var lastV = -1
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.v(TAG, "positionOffset: $positionOffset , positionOffsetPixels: $positionOffsetPixels")
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }
}