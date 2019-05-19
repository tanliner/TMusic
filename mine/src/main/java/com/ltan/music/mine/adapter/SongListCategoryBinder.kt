package com.ltan.music.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.SongListCategoryObject
import com.ltan.music.widget.ClickType
import com.ltan.music.widget.ListItemClickListener
import com.ltan.music.widget.ListItemViewClickListener
import com.ltan.music.widget.SongListCategoryItem
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: SongListCategoryBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class SongListCategoryBinder : ItemViewBinder<SongListCategoryObject, SongListCategoryBinder.ViewHolder>() {

    private var itemClick: ListItemClickListener? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {

        return ViewHolder(SongListCategoryItem(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SongListCategoryObject) {
        holder.item.setName(item.title)
        holder.item.setCount(item.count)
        holder.item.setCreateable(item.creatable)
        holder.item.setState(item.state)
        itemClick?.let { holder.item.setOnItemClickListener(ClickListener(it, holder)) }
    }

    fun setOnItemClick(l: ListItemClickListener) {
        itemClick = l
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as SongListCategoryItem
    }

    class ClickListener(itemClick: ListItemClickListener, holder: ViewHolder) : ListItemViewClickListener {
        private val click = itemClick
        private val h = holder
        override fun onClick(v: View, type: ClickType) {
            click.onItemClick(h.adapterPosition, h.itemView, type)
        }
    }
}