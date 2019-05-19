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
 * @ClassName: SongItem
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
class SongItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val number: TextView
    private val playing: ImageView
    private val title: TextView
    private val subTitle: TextView
    private val videoTag: ImageView
    private val menu: ImageView

    private var itemClickListener: ListItemViewClickListener? = null

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.song_item, this, true)
        number = findViewById(R.id.tv_song_item_number)
        playing = findViewById(R.id.iv_song_item_playing)
        title = findViewById(R.id.tv_song_item_title)
        subTitle = findViewById(R.id.tv_song_item_subtitle)
        videoTag = findViewById(R.id.iv_song_item_video)
        menu = findViewById(R.id.iv_song_item_menu)
        menu.setOnClickListener(this)
        videoTag.setOnClickListener(this)
        setOnClickListener(this)
    }

    fun setNumber(n: Int) {
        number.text = n.toString()
    }

    fun setTitle(t: String?) {
        title.text = t
    }

    fun setSubTitle(t: String?) {
        subTitle.text = t
    }

    fun setVideoTag(mv: Boolean) {
        if (mv) {
            videoTag.visibility = View.VISIBLE
        } else {
            videoTag.visibility = View.GONE
        }
    }

    fun setOnItemClickListener(l: ListItemViewClickListener) {
        itemClickListener = l
    }

    override fun onClick(v: View?) {
        when(v) {
            videoTag -> itemClickListener?.onClick(videoTag, ClickType.VIDEO)
            menu -> itemClickListener?.onClick(menu, ClickType.MENU)
            else -> itemClickListener?.onClick(this, ClickType.ITEM)
        }
    }
}