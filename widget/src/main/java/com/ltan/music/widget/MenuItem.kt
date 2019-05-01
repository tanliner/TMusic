package com.ltan.music.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: MenuItem
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-01
 * @Version: 1.0
 */
class MenuItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_TITLE_ALPHA = 0.6F
    }
    // View
    private val mTitleTv: TextView

    // variable
    private var mType: Int
    private var mTitle: String? = null

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.menu_item, this, true)

        // read from res/values/attrs.xml
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuItem)
        mType = ta.getInt(R.styleable.MenuItem_type, 0)
        ta.getString(R.styleable.MenuItem_android_text)?.let { mTitle = it }
        ta.recycle()

        mTitleTv = findViewById(R.id.tv_menu_item_title)
        mTitleTv.text = mTitle
        mTitleTv.alpha = DEFAULT_TITLE_ALPHA
    }

    fun setTitle(title: String?) {
        mTitle = title
        mTitleTv.text = title
    }

    fun setActiveState(status: Float) {
        mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, resources.getInteger(R.integer.menu_item_text_size) + (status * 6))
        mTitleTv.alpha = DEFAULT_TITLE_ALPHA + status * (1 - DEFAULT_TITLE_ALPHA)
    }
}