package com.ltan.music.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: SongListItem
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-07
 * @Version: 1.0
 */
class SongListItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private var mPrevImgIv: ImageView
    private var mItemNameTv: TextView
    private var mCountTv: TextView
    private var mHeartModeImgIv: ImageView
    private var mMenuImgIv: ImageView

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.song_list_item, this, true)

        mPrevImgIv = findViewById(R.id.iv_song_list_item_preview)
        mItemNameTv = findViewById(R.id.tv_song_list_item_name)
        mCountTv = findViewById(R.id.tv_song_list_item_count)
        mMenuImgIv = findViewById(R.id.iv_song_list_item_menu)
        mHeartModeImgIv = findViewById(R.id.iv_song_list_item_heart_mode)

        // val pTop = resources.getDimensionPixelSize(R.dimen.song_list_item_name_padding_v)
        // val pBottom = resources.getDimensionPixelSize(R.dimen.song_list_item_name_padding_v)
        val pLeft = resources.getDimensionPixelSize(R.dimen.song_list_item_left_padding)
        val pRight = resources.getDimensionPixelSize(R.dimen.song_list_item_right_padding)
        setPadding(pLeft, 0, pRight, 0)
    }
    fun setPreview(preview: Drawable?) {
        mPrevImgIv.setImageDrawable(preview)
    }
    fun setName(name: String) {
        mItemNameTv.text = name
    }

    fun setCount(count: Int) {
        val s = resources.getString(R.string.song_list_item_count)
        mCountTv.text = s.format(count)
    }

    fun setHeartMode(b: Boolean) {
        if (b) {
            mHeartModeImgIv.visibility = View.VISIBLE
            mMenuImgIv.visibility = View.GONE
        } else {
            mMenuImgIv.visibility = View.VISIBLE
            mHeartModeImgIv.visibility = View.GONE
        }
    }
}