package com.ltan.music.mine.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.basemvp.setValue
import com.ltan.music.common.MusicLog
import com.ltan.music.mine.PageItemObject
import com.ltan.music.mine.R
import com.ltan.music.mine.adapter.MineHeaderAdapter
import com.ltan.music.mine.contract.IMineContract
import com.ltan.music.mine.presenter.MinePresenter
import kotterknife.bindView

/**
 * TMusic.com.ltan.music.index.fragments
 *
 * @ClassName: MineFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-26
 * @Version: 1.0
 */
class MineFragment : BaseMVPFragment<MinePresenter>(), IMineContract.View {
    companion object {
        const val TAG = "Mine/Frag/"
        fun newInstance(): MineFragment {
            return MineFragment()
        }
    }

    var mHeader: View by bindView(R.id.page_header)
    var mRclView: RecyclerView by bindView(R.id.rclv_mine_header)

    override fun initLayout(): Int {
        return R.layout.mine_fragment
    }

    override fun testView(p: IMineContract.Presenter) {
        MusicLog.d(TAG, "testView called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // just a several log, TODO remove it
        mPresenter?.attachView(this)
        mPresenter?.start()
        mPresenter?.queryData()
        init()
    }

    private fun init() {
        mHeader.setOnClickListener { v -> testClick(v); testClick(mHeader) }
        mRclView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val headerAdapter = MineHeaderAdapter(mRclView)
        headerAdapter.setDataSource(generateItems())
        mRclView.adapter = headerAdapter
    }

    fun testClick(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }

    private fun generateItems(): ArrayList<PageItemObject> {
        val items = ArrayList<PageItemObject>()

        val drawableRes = intArrayOf(
            R.drawable.page_header_item_download,
            R.drawable.page_header_item_fm,
            R.drawable.page_header_item_identify,
            R.drawable.page_header_item_logo,
            R.drawable.page_header_item_search,
            R.drawable.page_header_item_star,
            R.drawable.page_header_item_star,
            R.drawable.page_header_item_identify,
            R.drawable.page_header_item_star,
            R.drawable.page_header_item_menu
        )
        val titleRes = intArrayOf(
            R.string.mine_header_shortcut_fm,
            R.string.mine_header_shortcut_sati,
            R.string.mine_header_shortcut_fav,
            R.string.mine_header_shortcut_chi_par,
            R.string.mine_header_shortcut_classic,
            R.string.mine_header_shortcut_run,
            R.string.mine_header_shortcut_xb,
            R.string.mine_header_shortcut_jazz,
            R.string.mine_header_shortcut_drive,
            R.string.mine_header_shortcut_menu
        )

        for (i in 0 until drawableRes.size) {
            val item = PageItemObject(drawableRes[i], getString(titleRes[i]))
            items.add(item)
        }
        return items
    }
}
