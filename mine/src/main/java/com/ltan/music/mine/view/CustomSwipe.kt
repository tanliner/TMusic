package com.ltan.music.mine.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * TMusic.com.ltan.music.mine.view
 *
 * @ClassName: CustomSwipe
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-22
 * @Version: 1.0
 */
class CustomSwipeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?
) : SwipeRefreshLayout(context, attrs) {

    interface OnInterceptListener {
        fun onInterceptTouchEvent(event: MotionEvent): Boolean
    }

    private var mTouchSlop: Int = 0
    private var mPrevX: Float = 0.toFloat()
    private var listener: OnInterceptListener? = null

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    fun setOnInterceptListener(listener: OnInterceptListener) {
        this.listener = listener
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (listener != null && !listener!!.onInterceptTouchEvent(event)) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> mPrevX = MotionEvent.obtain(event).x

            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = Math.abs(eventX - mPrevX)

                if (xDiff > mTouchSlop) {
                    return false
                }
            }
        }

        return super.onInterceptTouchEvent(event)
    }


}