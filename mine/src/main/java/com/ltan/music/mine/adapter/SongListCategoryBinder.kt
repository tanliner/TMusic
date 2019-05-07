package com.ltan.music.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.common.ToastUtil
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
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {

        return ViewHolder(SongListCategoryItem(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SongListCategoryObject) {
        holder.item.setName(item.title)
        holder.item.setCount(item.count)
        holder.item.setCreateable(item.creatable)
        listener = ClickListener()
        holder.item.setClickListener(listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as SongListCategoryItem
    }

    class ClickListener : SongListCategoryItem.ClickListener {
        override fun onClick(v: View?, type: SongListCategoryItem.ClickType) {
            ToastUtil.showToastShort("${type.tName} clicked")
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
        fun onItemClick(position: Int, type: SongListCategoryItem.ClickType)
    }
}