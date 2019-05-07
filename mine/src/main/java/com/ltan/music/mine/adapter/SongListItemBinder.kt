package com.ltan.music.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.SongListItemObject
import com.ltan.music.widget.SongListItem
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

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(SongListItem(ctx))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SongListItemObject) {
        holder.item.setPreview(ctx.getDrawable(item.imgId))
        holder.item.setName(item.title)
        holder.item.setCount(item.count)
        holder.item.setHeartMode(item.isHeartMode)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as SongListItem
    }
}