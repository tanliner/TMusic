package com.ltan.music.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * TMusic.com.ltan.music.mine.view
 *
 * @ClassName: CollectorItem
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class CollectorItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var mPrevImgIv: ImageView
    private var mItemNameTv: TextView
    private var mCountTv: TextView
    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.collector_item, this, true)
        mPrevImgIv = findViewById(R.id.iv_collector_item_preview)
        mItemNameTv = findViewById(R.id.tv_collector_item_name)
        mCountTv = findViewById(R.id.tv_collector_item_count)
    }

    fun setImage(img: Drawable?) {
        mPrevImgIv.setImageDrawable(img)
    }

    fun setName(name: String) {
        mItemNameTv.text = name
    }

    fun setCount(count: Int) {
        val s = "(%1s)"
        mCountTv.text = s.format(count)
    }
}