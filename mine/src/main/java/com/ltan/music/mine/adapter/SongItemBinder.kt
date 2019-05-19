package com.ltan.music.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.SongItemObject
import com.ltan.music.widget.ClickType
import com.ltan.music.widget.ListItemClickListener
import com.ltan.music.widget.ListItemViewClickListener
import com.ltan.music.widget.SongItem
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: SongItemBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
class SongItemBinder : ItemViewBinder<SongItemObject, SongItemBinder.ViewHolder>() {

    private var itemClick: ListItemClickListener? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val v = SongItem(inflater.context)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SongItemObject) {
        holder.item.setNumber(item.number)
        holder.item.setTitle(item.title)
        holder.item.setSubTitle(item.subTitle)
        holder.item.setVideoTag(item.video)
        // itemClick?.let { holder.item.setOnItemClickListener(SongItemClick(it, holder)) }
        itemClick?.let { holder.item.setOnItemClickListener(SongItemClick(it, holder)) }
    }

    fun setOnItemClickListener(l: ListItemClickListener) {
        itemClick = l
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as SongItem
    }

    private class SongItemClick(click: ListItemClickListener, holder: ViewHolder) : ListItemViewClickListener {
        private val l = click
        private var h = holder

        override fun onClick(v: View, type: ClickType) {
            l.onItemClick(h.adapterPosition, v, type)
        }
    }

}