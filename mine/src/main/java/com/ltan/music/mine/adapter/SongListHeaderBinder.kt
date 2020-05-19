package com.ltan.music.mine.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ltan.music.mine.R
import com.ltan.music.mine.SongListHeaderObject
import com.ltan.music.widget.MusicRecycleView
import com.ltan.music.widget.constants.PlayListItemPreview
import jp.wasabeef.glide.transformations.BlurTransformation
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: SongListHeaderBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-21
 * @Version: 1.0
 */
class SongListHeaderBinder : ItemViewBinder<SongListHeaderObject, SongListHeaderBinder.ViewHolder>(),
    MusicRecycleView.OnHeaderChangeListener {
    companion object {
        const val MIN_ALPHA = 0.6F
        const val MAX_ALPHA = 1F
        const val MAX_ALPHA_NUMBER = 255
    }

    private lateinit var mViewHolder: ViewHolder
    private lateinit var mFloatingHeader: ImageView
    private var bgColorUpdateOffset = 0F

    /**
     * Update background of header item when scrollY > [offset]
     */
    fun setOffset(offset: Float) {
        this.bgColorUpdateOffset = offset
    }

    fun setFloatingHeader(img: ImageView) {
        mFloatingHeader = img
        mFloatingHeader.setBackgroundColor(Color.TRANSPARENT)
        mFloatingHeader.imageAlpha = 0
    }

    override fun onScrollChanged(scrollY: Int) {
        var alpha = MAX_ALPHA * scrollY / bgColorUpdateOffset
        if (alpha >= MAX_ALPHA) {
            alpha = MAX_ALPHA
        }
        alpha = if (alpha >= MIN_ALPHA) { 1 - (alpha - MIN_ALPHA) } else MAX_ALPHA
        val drawable = mViewHolder.bgImg.drawable
        if (drawable != null) {
            drawable.mutate().alpha = (MAX_ALPHA_NUMBER * alpha).toInt()
            mViewHolder.bgImg.setImageDrawable(drawable)
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = inflater.inflate(R.layout.mine_song_list_header, parent, false)
        mViewHolder = ViewHolder(view)
        return mViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SongListHeaderObject) {
        val ctx = holder.itemView.context
        Glide.with(ctx)
            .load(item.previewUrl)
            .placeholder(PlayListItemPreview.SONG_LIST_PREVIEW)
            .error(PlayListItemPreview.SONG_LIST_PREVIEW)
            .into(holder.previewImg)
        holder.songListTitle.text = item.title
        holder.playAllTxt.text = ctx.resources.getString(R.string.mine_song_list_header_play_all)
        holder.songListSize.text = ctx.resources.getString(R.string.mine_song_list_header_song_count, item.songSize)
        holder.songListOwner.text = item.owner
        holder.songListExtra.text = item.extra

        Glide.with(ctx)
            .load(item.previewUrl)
            .skipMemoryCache(true)
            .centerCrop()
            // .transform(BlurTransformation(25, 38))
            .into(holder.bgImg)
        // floating header, out of the RecycleView
        Glide.with(ctx)
            .load(item.previewUrl)
            .skipMemoryCache(true)
            // .transform(BlurTransformation(25, 38))
            .into(mFloatingHeader)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bgImg: ImageView = itemView.findViewById(R.id.iv_header_transform_test)
        val previewImg: ImageView = itemView.findViewById(R.id.iv_song_list_header_preview)
        val songListTitle: TextView = itemView.findViewById(R.id.tv_song_list_header_title)
        val songListOwner: TextView = itemView.findViewById(R.id.tv_song_list_owner)
        val songListExtra: TextView = itemView.findViewById(R.id.tv_song_list_owner_extra_info)
        val playAllTxt: TextView = itemView.findViewById(R.id.tv_song_list_play_all)
        val playAllImg: ImageView = itemView.findViewById(R.id.iv_song_list_play_all)
        val songListSize: TextView = itemView.findViewById(R.id.tv_song_list_play_all_summary)
    }
}