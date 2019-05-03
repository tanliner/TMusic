package com.ltan.music.mine.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.basemvp.setValue
import com.ltan.music.common.MusicLog
import com.ltan.music.mine.CollectorItemObject
import com.ltan.music.mine.PageItemObject
import com.ltan.music.mine.R
import com.ltan.music.mine.SongListItemObject
import com.ltan.music.mine.adapter.CollectorItemBinder
import com.ltan.music.mine.adapter.EmptyItemBinder
import com.ltan.music.mine.adapter.MineHeaderBinder
import com.ltan.music.mine.adapter.SongListItemBinder
import com.ltan.music.mine.contract.IMineContract
import com.ltan.music.mine.presenter.MinePresenter
import kotterknife.bindView
import me.drakeet.multitype.MultiTypeAdapter

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

    private var mFooter: View by bindView(R.id.index_pagers_footer)
    var mRclView: RecyclerView by bindView(R.id.rclv_mine)

    private lateinit var mMultiAdapter: MultiTypeAdapter
    private lateinit var items: MutableList<Any>

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
        mFooter.setOnClickListener { v -> testClick(v); testClick(mFooter) }
        // mRclView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        mRclView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mMultiAdapter = MultiTypeAdapter()
        // recycle
        mMultiAdapter.register(String::class, MineHeaderBinder(requireContext(), generateHeaderItems()))
        mMultiAdapter.register(CollectorItemObject::class.java, CollectorItemBinder(requireContext()))
        mMultiAdapter.register(Integer::class.java, EmptyItemBinder())
        mMultiAdapter.register(SongListItemObject::class.java, SongListItemBinder())
        genItems()
        mMultiAdapter.items = items

        mRclView.adapter = mMultiAdapter
    }

    fun testClick(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }

    private fun generateHeaderItems(): ArrayList<PageItemObject> {
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

    private fun genItems() {
        items = ArrayList()
        items.add("") // recycle view
        items.addAll(genCollectorItems())
        items.add(0)
        items.addAll(genSongList())
    }

    /**
     * create collector items
     */
    private fun genCollectorItems(): ArrayList<CollectorItemObject> {
        val collectImgs = intArrayOf(
            R.drawable.t_actionbar_music_normal,
            R.drawable.t_actionbar_friends_normal,
            R.drawable.t_actionbar_discover_normal,
            R.drawable.t_actionbar_video_normal,
            R.drawable.t_actionbar_music_normal
        )
        val collectNames = intArrayOf(
            R.string.mine_content_collector_item_local,
            R.string.mine_content_collector_item_recent,
            R.string.mine_content_collector_item_download,
            R.string.mine_content_collector_item_my_fm,
            R.string.mine_content_collector_item_my_fav
        )
        val collectors = ArrayList<CollectorItemObject>()
        for (i in 1..5) {
            val title = getString(collectNames[i - 1])
            val item = CollectorItemObject(collectImgs[i - 1], title, i)
            collectors.add(item)
        }
        return collectors
    }

    private fun genSongList(): ArrayList<SongListItemObject> {
        val list = ArrayList<SongListItemObject>()
        val titles = intArrayOf(
            R.string.mine_song_list_item_created,
            R.string.mine_song_list_item_favorite
        )
        for (i in 1..2) {
            val item = SongListItemObject(getString(titles[i - 1]), i * 4)
            if (i == 1) {
                item.creatable = true
            }
            list.add(item)
        }
        return list
    }
}
