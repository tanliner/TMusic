package com.ltan.music.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
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
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var mPrevImgIv: ImageView
    private var mItemNameTv: TextView
    private var mCountTv: TextView
    private var mHeartModeLl: LinearLayout? = null
    private var mMenuIv: ImageView

    private var mHeartModeStub: ViewStub

    private var itemClickListener: ListItemViewClickListener? = null

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.song_list_item, this, true)

        mPrevImgIv = findViewById(R.id.iv_song_list_item_preview)
        mItemNameTv = findViewById(R.id.tv_song_list_item_name)
        mCountTv = findViewById(R.id.tv_song_list_item_count)
        mMenuIv = findViewById(R.id.iv_song_list_item_menu)
        mHeartModeStub = findViewById(R.id.stub_heart_mode)

        // val pTop = resources.getDimensionPixelSize(R.dimen.song_list_item_name_padding_v)
        // val pBottom = resources.getDimensionPixelSize(R.dimen.song_list_item_name_padding_v)
        val pLeft = resources.getDimensionPixelSize(R.dimen.song_list_item_left_padding)
        val pRight = resources.getDimensionPixelSize(R.dimen.song_list_item_right_padding)
        val height = resources.getDimensionPixelSize(R.dimen.song_list_item_height)
        val lp = ViewGroup.LayoutParams(-1, height)
        layoutParams = lp

        setPadding(pLeft, 0, pRight, 0)

        mMenuIv.setOnClickListener(this)
        setOnClickListener(this)
    }

    fun setPreview(preview: Drawable?) {
        mPrevImgIv.setImageDrawable(preview)
    }

    fun setName(name: String?) {
        mItemNameTv.text = name
    }

    fun setCount(count: Int) {
        val s = resources.getString(R.string.song_list_item_count)
        mCountTv.text = s.format(count)
    }

    fun setHeartMode(hearMode: Boolean) {
        if (hearMode) {
            var vInStub: LinearLayout? = mHeartModeLl
            if(mHeartModeLl == null) {
                vInStub = mHeartModeStub.inflate() as LinearLayout
                vInStub.setOnClickListener(this)
            }
            vInStub?.visibility = View.VISIBLE
            mHeartModeLl = vInStub
            mMenuIv.visibility = View.GONE
        } else {
            mMenuIv.visibility = View.VISIBLE
            mHeartModeLl?.visibility = View.GONE
        }
    }

    fun setOnItemClickListener(l: ListItemViewClickListener) {
        itemClickListener = l
    }

    override fun onClick(v: View?) {
        if(itemClickListener == null) {
            return
        }
        val click = itemClickListener!!
        when {
            mHeartModeLl != null && v == mHeartModeLl -> click.onClick(mHeartModeLl!!, ClickType.HEART)
            v == mMenuIv -> click.onClick(mMenuIv, ClickType.MENU)
            else -> click.onClick(this, ClickType.ITEM)
        }
    }
}