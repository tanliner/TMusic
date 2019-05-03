package com.ltan.music.widget

import android.content.Context
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
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class SongListItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    enum class ClickType(val tName: String) {
        MENU("menu"), CREATOR("plus"), ITEM("item");
    }

    override fun onClick(v: View?) {
        when(v) {
            mMenuImgIv -> listener.onClick(v, ClickType.MENU)
            mCreateImgIv -> listener.onClick(v, ClickType.CREATOR)
            this -> listener.onClick(v, ClickType.ITEM)
        }
    }
    private lateinit var listener: ClickListener

    private var mPrevImgIv: ImageView
    private var mItemNameTv: TextView
    private var mCountTv: TextView
    private var mCreateImgIv: ImageView
    private var mMenuImgIv: ImageView

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.song_list_item, this, true)
        mPrevImgIv = findViewById(R.id.iv_song_list_item_preview)
        mItemNameTv = findViewById(R.id.tv_song_list_item_name)
        mCountTv = findViewById(R.id.tv_song_list_item_count)
        mMenuImgIv = findViewById(R.id.iv_song_list_item_menu)
        mCreateImgIv = findViewById(R.id.iv_song_list_item_create)
        // val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val pTop = resources.getDimensionPixelSize(R.dimen.song_list_item_name_padding_v)
        val pBottom = resources.getDimensionPixelSize(R.dimen.song_list_item_name_padding_v)
        val pLeft = resources.getDimensionPixelSize(R.dimen.song_list_item_left_padding)
        val pRight = resources.getDimensionPixelSize(R.dimen.song_list_item_right_padding)
        setPadding(pLeft, pTop, pRight, pBottom)

        mPrevImgIv.setImageDrawable(context.getDrawable(R.drawable.right_arrow_little))
        setOnClickListener(this)
        mMenuImgIv.setOnClickListener(this)
        mCreateImgIv.setOnClickListener(this)
    }

    fun setName(name: String) {
        mItemNameTv.text = name
    }

    fun setCount(count: Int) {
        val s = "(%1s)"
        mCountTv.text = s.format(count)
    }

    fun setClickListener(listener: ClickListener) {
        this.listener = listener
    }

    fun setCreateable(b: Boolean) {
        if (b) {
            mCreateImgIv.visibility = View.VISIBLE
        } else {
            mCreateImgIv.visibility = View.GONE
        }
    }

    interface ClickListener {
        /**
         * [v] which view has been clicked
         * [type] as the [v], a useful Integer value
         */
        fun onClick(v: View?, type: ClickType)
    }
}