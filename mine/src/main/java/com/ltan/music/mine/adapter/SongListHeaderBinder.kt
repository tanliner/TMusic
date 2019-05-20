package com.ltan.music.mine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.R
import me.drakeet.multitype.ItemViewBinder

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: SongListHeaderBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-21
 * @Version: 1.0
 */
class SongListHeaderBinder : ItemViewBinder<String, SongListHeaderBinder.ViewHolder>() {

    interface BackListener {
        fun onBack()
    }

    private var mBackClick: BackListener? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val context = inflater.context
        val title = context.resources.getString(R.string.mine_song_list_back)
        val button = Button(context)
        button.text = title
        return ViewHolder(button)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.item.setOnClickListener { mBackClick?.onBack() }
    }

    fun setBackListener(l: BackListener) {
        mBackClick = l
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView as Button
    }
}