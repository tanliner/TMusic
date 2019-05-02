package com.ltan.music.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: PageHeaderItem
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-02
 * @Version: 1.0
 */
class PageHeaderItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mPrevImgView: ImageView
    private var mTipsView: TextView

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.page_header_item, this, true)

        mPrevImgView = findViewById(R.id.iv_page_header_item)
        mTipsView = findViewById(R.id.tv_page_header_item)
    }

    fun setTitle(t: String) {
        mTipsView.text = t
    }

    fun setPreviewImg(v: Drawable) {
        mPrevImgView.setImageDrawable(v)
    }

    fun getTitleView(): TextView {
        return mTipsView
    }
    fun getImgView(): ImageView {
        return mPrevImgView
    }
}