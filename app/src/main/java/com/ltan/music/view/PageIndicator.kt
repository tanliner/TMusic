package com.ltan.music.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.ltan.music.common.MusicLog

/**
 * TMusic.com.ltan.music.view
 *
 * @ClassName: PageIndicator
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-27
 * @Version: 1.0
 */
class PageIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "ltan/PageIndicator"
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
                MusicLog.v(TAG, "positionOffset: $positionOffset , positionOffsetPixels: $positionOffsetPixels")
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }
}