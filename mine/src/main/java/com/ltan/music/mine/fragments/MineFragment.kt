package com.ltan.music.mine.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.basemvp.setValue
import com.ltan.music.common.MusicLog
import com.ltan.music.mine.*
import com.ltan.music.mine.adapter.*
import com.ltan.music.mine.contract.IMineContract
import com.ltan.music.mine.presenter.MinePresenter
import com.ltan.music.widget.SongListCategoryItem
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
        const val ID_CATEGORY_CREATED_LIST = 0
        const val ID_CATEGORY_FAVORITE_LIST = 1
        fun newInstance(): MineFragment {
            return MineFragment()
        }
    }

    private var mFooter: View by bindView(R.id.index_pagers_footer)
    var mRclView: RecyclerView by bindView(R.id.rclv_mine)

    private lateinit var mMultiAdapter: MultiTypeAdapter
    private lateinit var items: MutableList<Any>

    private var createdCategory = ArrayList<SongListItemObject>()
    private var favoriteCategory = ArrayList<SongListItemObject>()

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
        val categoryBinder = SongListCategoryBinder()
        mMultiAdapter.register(SongListCategoryObject::class.java, categoryBinder)
        mMultiAdapter.register(SongListItemObject::class.java, SongListItemBinder(requireContext()))
        genItems()
        mMultiAdapter.items = items

        categoryBinder.setOnItemClick(CategoryClickListener(items, createdCategory, favoriteCategory, mMultiAdapter))

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
        items.addAll(getSongCategories())
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

    private fun getSongCategories(): ArrayList<Any> {
        val list = ArrayList<Any>()
        val item = SongListCategoryObject(ID_CATEGORY_CREATED_LIST, getString(R.string.mine_song_list_category_created), 10, true)
        list.add(item)
        createdCategory = getSongList()
        list.addAll(createdCategory)

        val item2 = SongListCategoryObject(ID_CATEGORY_FAVORITE_LIST, getString(R.string.mine_song_list_category_favorite), 3)
        list.add(item2)
        favoriteCategory = getSongList2()
        list.addAll(favoriteCategory)

        return list
    }

    private fun getSongList(): ArrayList<SongListItemObject> {
        val list = ArrayList<SongListItemObject>()
        val drawableRes = intArrayOf(
            R.drawable.page_header_item_download,
            R.drawable.page_header_item_fm,
            R.drawable.page_header_item_identify,
            R.drawable.page_header_item_logo,
            R.drawable.page_header_item_search,
            R.drawable.page_header_item_download,
            R.drawable.page_header_item_fm,
            R.drawable.page_header_item_identify,
            R.drawable.page_header_item_logo,
            R.drawable.page_header_item_search
        )
        val titles = intArrayOf(
            R.string.mine_song_list_item_favorite,
            R.string.mine_song_list_item_test,
            R.string.mine_song_list_item_test,
            R.string.mine_song_list_item_test,
            R.string.mine_song_list_item_ldm,
            R.string.mine_song_list_item_mine,
            R.string.mine_song_list_item_ftm,
            R.string.mine_song_list_item_english,
            R.string.mine_song_list_item_bee,
            R.string.mine_song_list_item_try
        )
        // page_header_item_logo
        for (i in 1..titles.size) {
            val item = SongListItemObject(drawableRes[i - 1], getString(titles[i - 1]), i * 3)
            if (i == 1) {
                item.isHeartMode = true
            }
            list.add(item)
        }

        return list
    }

    private fun getSongList2(): ArrayList<SongListItemObject> {
        val list = ArrayList<SongListItemObject>()
        val drawableRes = intArrayOf(
            R.drawable.page_header_item_download,
            R.drawable.page_header_item_fm,
            R.drawable.page_header_item_identify
        )
        val titles = intArrayOf(
            R.string.mine_song_list_item_favorite,
            R.string.mine_song_list_item_bee,
            R.string.mine_song_list_item_try
        )

        for (i in 1..titles.size) {
            val item = SongListItemObject(drawableRes[i - 1], getString(titles[i - 1]), i * 3)
            list.add(item)
        }

        return list
    }

    class CategoryClickListener(
        private val items: MutableList<Any>,
        private val category1: ArrayList<SongListItemObject>,
        private val category2: ArrayList<SongListItemObject>,
        private val adapter: MultiTypeAdapter
    ) : SongListCategoryBinder.OnItemClickListener {
        override fun onItemClick(position: Int, type: SongListCategoryItem.ClickType) {
            val item = items[position] as SongListCategoryObject
            // this index will update, click the items container
            val index = position + 1
            var dataSource = category1
            if (item.id == ID_CATEGORY_FAVORITE_LIST) {
                dataSource = category2
            }

            if (item.state == SongListCategoryObject.STATE.EXPAND) {
                item.state = SongListCategoryObject.STATE.SHRINK
                items.removeAll(dataSource)
                adapter.notifyItemRangeRemoved(index, dataSource.size)
            } else {
                item.state = SongListCategoryObject.STATE.EXPAND
                items.addAll(index, dataSource)
                adapter.notifyItemRangeInserted(index, dataSource.size)
            }
        }
    }
}
