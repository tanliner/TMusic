package com.ltan.music.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.CollectorItemObject
import com.ltan.music.widget.CollectorItem
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: CollectorItemBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class CollectorItemBinder(context: Context) : ItemViewBinder<CollectorItemObject, CollectorItemBinder.ViewHolder>() {

    private val ctx = context
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(CollectorItem(inflater.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CollectorItemObject) {
        // ctx.getDrawable(item.mImgId)?.let { holder.item.setImage(it) }
        holder.item.setImage(ctx.getDrawable(item.mImgId))
        holder.item.setName(item.mTitle)
        holder.item.setCount(item.count)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as CollectorItem
    }

    /**
     * recycle view item-click listener
     * TODO exposure out to fragment
     */
    interface OnItemClickListener {
        /**
         * push the position
         */
        fun onItemClick(position: Int)
    }
}