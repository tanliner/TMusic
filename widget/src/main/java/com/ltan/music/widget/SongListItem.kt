package com.ltan.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: SongListItem
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class SongListItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var mPrevImgIv: ImageView
    private var mItemNameTv: TextView
    private var mCountTv: TextView
    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.song_list_item, this, true)
        mPrevImgIv = findViewById(R.id.iv_song_list_item_preview)
        mItemNameTv = findViewById(R.id.tv_song_list_item_name)
        mCountTv = findViewById(R.id.tv_song_list_item_count)

        mPrevImgIv.setImageDrawable(context.getDrawable(R.drawable.right_arrow_little))
    }

    fun setName(name: String) {
        mItemNameTv.text = name
    }

    fun setCount(count: Int) {
        val s = "(%1s)"
        mCountTv.text = s.format(count)
    }
}