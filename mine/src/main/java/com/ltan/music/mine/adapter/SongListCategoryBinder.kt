package com.ltan.music.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.SongListCategoryObject
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

    private lateinit var listener: ClickListener
    private lateinit var itemClick: OnItemClickListener
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {

        return ViewHolder(SongListCategoryItem(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SongListCategoryObject) {
        holder.item.setName(item.title)
        holder.item.setCount(item.count)
        holder.item.setCreateable(item.creatable)
        holder.item.setState(item.state)
        listener = ClickListener(this, holder)
        holder.item.setClickListener(listener)
    }

    fun setOnItemClick(l: OnItemClickListener) {
        itemClick = l
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as SongListCategoryItem
    }

    class ClickListener(b: SongListCategoryBinder, h: ViewHolder) : SongListCategoryItem.ClickListener {
        private val binder = b
        private val holder = h
        override fun onClick(v: View, type: SongListCategoryItem.ClickType) {
            binder.itemClick.onItemClick(binder.getPosition(holder), holder.itemView, type)
        }
    }

    /**
     * recycle view item-click listener
     * TODO exposure out to fragment
     */
    interface OnItemClickListener {
        /**
         * push the position
         */
        fun onItemClick(position: Int, view: View, type: SongListCategoryItem.ClickType)
    }
}