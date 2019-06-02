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
        fun onScrollChanged(scrollY: Int)
    }

    private var mChangeListeners: ArrayList<OnHeaderChangeListener> = ArrayList()

    private var mScrollY = 0

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mScrollY += dy
                if(mChangeListeners.isNotEmpty()) {
                    for (l in mChangeListeners) {
                        l.onScrollChanged(mScrollY)
                    }
                }
            }
        })
    }

    fun addChangeListener(l: OnHeaderChangeListener) {
        mChangeListeners.add(l)
    }
}