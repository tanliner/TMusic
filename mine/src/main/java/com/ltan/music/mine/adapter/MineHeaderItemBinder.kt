package com.ltan.music.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.PageItemObject
import com.ltan.music.widget.PageHeaderItem
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: MineHeaderItemBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class MineHeaderItemBinder(context: Context) : ItemViewBinder<PageItemObject, MineHeaderItemBinder.ViewHolder>() {

    private val ctx = context
    private var mItemClick: OnItemClickListener? = null
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val item = PageHeaderItem(inflater.context)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PageItemObject) {
        val itemView = holder.itemView as PageHeaderItem
        itemView.setTitle(item.mTitle)
        // can check the null like this below
        // ctx.getDrawable(item.mImgId)?.let { itemView.setPreviewImg(it) }
        itemView.setPreviewImg(ctx.getDrawable(item.mImgId))
        itemView.setOnClickListener {
            mItemClick?.onItemClick(getPosition(holder))
        }
    }

    fun setOnItemClickListener(l: OnItemClickListener) {
        mItemClick = l
    }

    class ViewHolder(itemView: PageHeaderItem) : RecyclerView.ViewHolder(itemView) {}

    /**
     * recycle view item-click listener
     */
    interface OnItemClickListener {
        /**
         * push the position
         */
        fun onItemClick(position: Int)
    }
}