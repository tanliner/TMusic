package com.ltan.music.mine.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.mine.PageItemObject
import com.ltan.music.widget.PageHeaderItem

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: MineHeaderAdapter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-02
 * @Version: 1.0
 */
class MineHeaderAdapter constructor(cylView: RecyclerView) : RecyclerView.Adapter<MineHeaderAdapter.ViewHolder>() {

    private val mDataSource: ArrayList<PageItemObject> = ArrayList()
    private val mContext: Context = cylView.context

    // public constructor(cylView: RecyclerView) {
    //     mContext = cylView.context
    //     mDataSource = ArrayList()
    // }

    fun setDataSource(source: ArrayList<PageItemObject>) {
        mDataSource.clear()
        mDataSource.addAll(source)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // val inflater = LayoutInflater.from(mContext)
        val item = PageHeaderItem(mContext)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return mDataSource.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.itemView as PageHeaderItem
        itemView.setTitle(mDataSource[position].mTitle)
        itemView.setPreviewImg(mContext.getDrawable(mDataSource[position].mImgId))
        //holder.titleTv.text = mDataSource[position]
    }


    class ViewHolder(itemView: PageHeaderItem) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.getTitleView()
        val previewImg: ImageView = itemView.getImgView()

        init {
            // titleTv = itemView.findViewById(R.id.tv_page_header_item)
        }
    }
}