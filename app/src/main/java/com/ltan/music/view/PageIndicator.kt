package com.ltan.music.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.ltan.music.common.MusicLog
import com.ltan.music.common.SimpleAnimator
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
        const val PAGER_DURATION = 250L
        const val PAGER_SCROLL_OFFSET_MAX = 1.0F
        const val PAGER_SCROLL_OFFSET_MIN = 0.0F
    }

    private val mContext: Context = context
    private var mIndicator: LinearLayout = LinearLayout(context)
    private var mIndicators: ArrayList<MenuItem>? = null
    private var mLeft: View? = null
    private var mRight: View? = null
    private lateinit var mViewPager: ViewPager
    // click the menu to swipe page
    private var isMenuItemClicked: Boolean

    // like a constructor in java
    init {
        // init center items
        mIndicator.layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        mIndicator.gravity = Gravity.CENTER
        mIndicator.orientation = LinearLayout.HORIZONTAL
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.addRule(CENTER_HORIZONTAL)
        addView(mIndicator, lp)
        isMenuItemClicked = false
    }

    fun addIndicators(menus: ArrayList<MenuItem>) {
        mIndicator.removeAllViews()
        mIndicators?.clear()
        for ((index, it) in menus.withIndex()) {
            it.setOnClickListener(HeaderClickListener(this, mViewPager, index))
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
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                MusicLog.v("positionOffset: $positionOffset , positionOffsetPixels: $positionOffsetPixels")
                if (isMenuItemClicked) {
                    // click the menu item, do animation directly
                    return
                }
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

    class HeaderClickListener constructor(pageIndicator: PageIndicator, vp: ViewPager, index: Int) : OnClickListener {
        private val mViewPager = vp
        private val item = index
        private val indicator = pageIndicator

        override fun onClick(v: View?) {
            if (mViewPager.currentItem == item) {
                return
            }
            indicator.isMenuItemClicked = true
            val fromPage = indicator.getIndicator(mViewPager.currentItem)
            val toPage = indicator.getIndicator(item)
            fromPage?.let {
                val animF = PageHeaderAnimation.generateAnim(it, PAGER_SCROLL_OFFSET_MAX, PAGER_SCROLL_OFFSET_MIN)
                animF.addListener(object : SimpleAnimator() {
                    override fun onAnimationEnd(animation: Animator?) {
                        indicator.isMenuItemClicked = false
                    }
                })
                animF.start()
            }

            toPage?.let {
                val animT = PageHeaderAnimation.generateAnim(it, PAGER_SCROLL_OFFSET_MIN, PAGER_SCROLL_OFFSET_MAX)
                animT.start()
            }
            mViewPager.setCurrentItem(item, true)
        }
    }

    object PageHeaderAnimation {
        @JvmStatic
        fun generateAnim(target: MenuItem, from: Float, to: Float): ValueAnimator {
            val va = ValueAnimator.ofFloat(from, to)
            va.duration = PAGER_DURATION
            va.addUpdateListener { v ->
                val curValue = v.animatedValue as Float
                target.setActiveState(curValue)
            }
            return va
        }
    }
}