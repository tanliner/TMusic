package com.ltan.music.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.HorizontalLine
import com.ltan.music.mine.R
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * Note: MultiView type cannot handle the digit 0 to Kotlin.Int
 *
 * @ClassName: HorizontalLineBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class HorizontalLineBinder : ItemViewBinder<HorizontalLine, HorizontalLineBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.mine_fragment_hor_spage, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: HorizontalLine) {}

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}