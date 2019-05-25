package com.ltan.music.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ltan.music.mine.SongListItemObject
import com.ltan.music.widget.ClickType
import com.ltan.music.widget.ListItemClickListener
import com.ltan.music.widget.ListItemViewClickListener
import com.ltan.music.widget.SongListItem
import com.ltan.music.widget.constants.PlayListItemPreview
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: SongListItemBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-08
 * @Version: 1.0
 */
class SongListItemBinder(context: Context) : ItemViewBinder<SongListItemObject, SongListItemBinder.ViewHolder>() {

    private val ctx = context
    private var itemClick: ListItemClickListener? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        // viewClick = ItemClick(itemClick, null)
        return ViewHolder(SongListItem(inflater.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SongListItemObject) {
        if(item.imgId > 0) {
            holder.item.setPreview(ctx.getDrawable(item.imgId))
        } else {
            Glide.with(ctx)
                .load(item.imgUrl)
                .error(PlayListItemPreview.ERROR_IMG)
                .placeholder(PlayListItemPreview.PLACEHOLDER_IMG)
                .into(holder.item.mPrevImgIv)
        }
        holder.item.setName(item.title)
        holder.item.setCount(item.count)
        holder.item.setHeartMode(item.isHeartMode)
        itemClick?.let { holder.item.setOnItemClickListener(SongListItemClick(it, holder)) }
    }

    fun setOnItemClick(l: ListItemClickListener) {
        itemClick = l
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as SongListItem
    }

    private class SongListItemClick(click: ListItemClickListener, holder: ViewHolder) : ListItemViewClickListener {
        private val l = click
        private var h = holder

        override fun onClick(v: View, type: ClickType) {
            l.onItemClick(h.adapterPosition, v, type)
        }
    }
}