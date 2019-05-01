package com.ltan.music.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.ltan.music.common.MusicLog
import com.ltan.music.widget.MenuItem

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
) : RelativeLayout(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "App/PageIndic"
    }

    private val mContext: Context = context
    private var mIndicator: LinearLayout = LinearLayout(context)
    private var mIndicators: ArrayList<MenuItem>? = null
    private var mLeft: View? = null
    private var mRight: View? = null
    private lateinit var mViewPager: ViewPager

    // like a constructor in java
    init {
        // init center items
        mIndicator.layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        mIndicator.gravity = Gravity.CENTER
        mIndicator.orientation = LinearLayout.HORIZONTAL
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.addRule(CENTER_HORIZONTAL)
        addView(mIndicator, lp)
    }

    fun addIndicators(menus: ArrayList<MenuItem>) {
        mIndicator.removeAllViews()
        mIndicators?.clear()
        for (it in menus) {
            mIndicator.addView(it)
        }
        mIndicators = menus
    }

    fun setLeft(left: View) {
        mLeft = left
        addView(left, 0)
    }

    fun setRight(right: View) {
        mRight = right
        if (childCount == 0) {
            addView(right)
        } else {
            addView(right, childCount - 1)
        }
    }

    fun removeRight() {
        if (mRight != null) {
            removeView(mRight)
        }
        mRight = null
    }

    fun removeLeft() {
        if (mLeft != null) {
            removeView(mLeft)
        }
        mLeft = null
    }

    fun setViewPager(pager: ViewPager) {
        mViewPager = pager
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var lastV = -1
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                MusicLog.v(TAG, "positionOffset: $positionOffset , positionOffsetPixels: $positionOffsetPixels")
                val leftItem = getIndicator(position)
                val rightItem = getIndicator(position + 1)
                leftItem?.setActiveState(1 - positionOffset)
                rightItem?.setActiveState(positionOffset)
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    fun getIndicator(index: Int): MenuItem? {
        if (index > mIndicator.childCount - 1) {
            return null
        }
        return mIndicators?.get(index)
    }
}