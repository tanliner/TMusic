package com.ltan.music.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.PlaceItem
import com.ltan.music.mine.R
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: PlaceItemBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
class PlaceItemBinder : ItemViewBinder<PlaceItem, PlaceItemBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val tv = TextView(inflater.context)
        tv.minHeight = inflater.context.resources.getDimensionPixelSize(R.dimen.bottom_media_control_height)
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PlaceItem) {}

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}