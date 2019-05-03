package com.ltan.music.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.common.ToastUtil
import com.ltan.music.mine.PageItemObject
import com.ltan.music.mine.R
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.MultiTypeAdapter

/**
 * TMusic.com.ltan.music.mine.adapter
 *
 * @ClassName: MineHeaderBinder
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class MineHeaderBinder(context: Context, dataS: ArrayList<PageItemObject>) : ItemViewBinder<String, MineHeaderBinder.ViewHolder>() {

    private val mData = dataS
    private val ctx = context
    private lateinit var mHeaderAdapter: MultiTypeAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val mItemClickListener = ItemClickListener(dataS)

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = inflater.inflate(R.layout.mine_fragment_header, parent, false)
        mHeaderAdapter = MultiTypeAdapter()
        val headerItemBinder = MineHeaderItemBinder(ctx)
        headerItemBinder.setOnItemClickListener(mItemClickListener)
        mHeaderAdapter.register(PageItemObject::class.java, headerItemBinder)
        layoutManager = LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.rclView?.layoutManager = layoutManager
        holder.rclView?.adapter = mHeaderAdapter

        mHeaderAdapter.items = mData
        mHeaderAdapter.notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rclView: RecyclerView? = itemView.findViewById(R.id.rclv_mine_header)
    }

    class ItemClickListener(items: ArrayList<PageItemObject>) : MineHeaderItemBinder.OnItemClickListener {
        private val dataList = items
        override fun onItemClick(position: Int) {
            ToastUtil.showToastShort(dataList[position].mTitle)
        }
    }
}