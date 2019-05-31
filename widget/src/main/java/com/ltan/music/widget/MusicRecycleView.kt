package com.ltan.music.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: MusicRecycleView
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-01
 * @Version: 1.0
 */
class MusicRecycleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "TestRecycleView"
    }

    interface OnHeaderChangeListener {
        fun onVisibleChange(visible: Boolean)
    }

    private var mChangeListener: OnHeaderChangeListener? = null
    private var mFloatingOffset = 0

    private var mScrollY = 0

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mScrollY += dy
                mChangeListener?.onVisibleChange(mScrollY >= mFloatingOffset)
            }
        })
    }

    fun setChangeListener(l: OnHeaderChangeListener) {
        mChangeListener = l
    }

    fun setFloatingOffset(offset: Int) {
        mFloatingOffset = offset
    }
}