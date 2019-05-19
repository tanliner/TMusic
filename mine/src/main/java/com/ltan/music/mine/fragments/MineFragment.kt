package com.ltan.music.mine.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.account.utils.AccountUtil
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.basemvp.setValue
import com.ltan.music.common.MusicLog
import com.ltan.music.common.ToastUtil
import com.ltan.music.mine.*
import com.ltan.music.mine.adapter.*
import com.ltan.music.mine.beans.PlayList
import com.ltan.music.mine.beans.PlayListDetailRsp
import com.ltan.music.mine.beans.SongSubCunt
import com.ltan.music.mine.contract.IMineContract
import com.ltan.music.mine.presenter.MinePresenter
import com.ltan.music.widget.SongListCategoryItem
import com.ltan.music.widget.constants.State
import kotterknife.bindView
import me.drakeet.multitype.MultiTypeAdapter
import kotlin.math.min

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
        const val CATEGORY_LIST_INDEX = 8
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

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.subcount()
        AccountUtil.getAccountInfo()?.let { mPresenter.getPlayList(it.id) }
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
        val songListBinder = SongListItemBinder(requireContext())
        mMultiAdapter.register(SongListItemObject::class.java, songListBinder)
        genItems()
        mMultiAdapter.items = items

        categoryBinder.setOnItemClick(CategoryClickListener(items, createdCategory, favoriteCategory, mMultiAdapter))
        songListBinder.setOnItemClick(SongListClickListener(items, mPresenter))

        mRclView.adapter = mMultiAdapter
    }

    private var artistCount = 0
    private var createdPlaylistCount = 0
    private var subPlaylistCount = 0
    private lateinit var collectionList: ArrayList<CollectorItemObject>
    private lateinit var songPlayList: ArrayList<SongListCategoryObject>

    override fun onSubcount(data: SongSubCunt?) {
        if(data == null) {
            ToastUtil.showToastShort(getString(R.string.mine_song_list_failed))
            return
        }
        MusicLog.d(TAG, "data is: $data")
        artistCount = data.artistCount
        createdPlaylistCount = data.createdPlaylistCount
        subPlaylistCount = data.subPlaylistCount

        collectionList[4].count = data.artistCount
        songPlayList[0].count = data.createdPlaylistCount
        songPlayList[1].count = data.subPlaylistCount
        mMultiAdapter.notifyDataSetChanged()
        // mMultiAdapter.notifyItemRangeChanged(5, 4)
    }

    override fun onPlayList(data: List<PlayList>?) {
        if(data == null) {
            ToastUtil.showToastShort(getString(R.string.mine_play_list_failed))
            return
        }
        MusicLog.d(TAG, "play list is: $data")
        val imgId = R.drawable.page_header_item_search

        // category created
        for (i in 0 until min(createdPlaylistCount, data.size)) {
            val item = SongListItemObject(data[i].id, imgId, data[i].name, data[i].trackCount)
            createdCategory.add(item)
        }
        if(createdPlaylistCount > 0) {
            createdCategory[0].isHeartMode = true;
        }
        // category subscribed
        for (i in createdPlaylistCount until data.size) {
            val item = SongListItemObject(data[i].id, imgId, data[i].name, data[i].trackCount)
            favoriteCategory.add(item)
        }
        var favoriteOffset = CATEGORY_LIST_INDEX + 1
        if(songPlayList[0].state == State.EXPAND) {
            favoriteOffset += createdPlaylistCount
            items.addAll(CATEGORY_LIST_INDEX, createdCategory)
        }
        if(songPlayList[1].state == State.EXPAND) {
            items.addAll(favoriteOffset, favoriteCategory)
        }
        mMultiAdapter.notifyDataSetChanged()
        // mMultiAdapter.notifyItemRangeInserted(CATEGORY_LIST_INDEX, createdPlaylistCount + subPlaylistCount)
    }

    override fun onPlayListDetail(data: PlayListDetailRsp?) {
        if(data == null) {
            ToastUtil.showToastShort(getString(R.string.mine_play_list_failed))
            return
        }
        //data.playlist
        MusicLog.d(TAG, "view onPlayListDetail: ${data.playlist}")
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
        collectionList = genCollectorItems()
        songPlayList = getSongCategories()

        items = ArrayList()
        items.add("") // recycle view
        items.addAll(collectionList)
        items.add(0)
        items.addAll(songPlayList)
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
        for (i in 0 until collectImgs.size) {
            val title = getString(collectNames[i])
            val item = CollectorItemObject(collectImgs[i], title, 0)
            collectors.add(item)
        }
        return collectors
    }

    private fun getSongCategories(): ArrayList<SongListCategoryObject> {
        val list = ArrayList<SongListCategoryObject>()
        val category1 = SongListCategoryObject(ID_CATEGORY_CREATED_LIST, getString(R.string.mine_song_list_category_created), 0, true)
        category1.state = State.COLLAPSE
        list.add(category1)

        val category2 = SongListCategoryObject(ID_CATEGORY_FAVORITE_LIST, getString(R.string.mine_song_list_category_favorite), 0)
        category2.state = State.COLLAPSE
        list.add(category2)
        return list
    }

    class CategoryClickListener(
        private val items: MutableList<Any>,
        private val category1: ArrayList<SongListItemObject>,
        private val category2: ArrayList<SongListItemObject>,
        private val adapter: MultiTypeAdapter
    ) : SongListCategoryBinder.OnItemClickListener {
        override fun onItemClick(position: Int, view: View, type: SongListCategoryItem.ClickType) {
            if (type == SongListCategoryItem.ClickType.ITEM) {
                itemClick(position, view)
            }
        }

        private fun itemClick(position: Int, view: View) {
            val item = items[position] as SongListCategoryObject
            // this index will update, click the items container
            val index = position + 1
            var dataSource = category1
            if (item.id == ID_CATEGORY_FAVORITE_LIST) {
                dataSource = category2
            }
            val itemView = view as SongListCategoryItem

            if (itemView.mState == State.EXPAND) {
                items.addAll(index, dataSource)
                adapter.notifyItemRangeInserted(index, dataSource.size)
            } else {
                items.removeAll(dataSource)
                adapter.notifyItemRangeRemoved(index, dataSource.size)
            }
        }
    }

    class SongListClickListener(
        private val items: MutableList<Any>,
        private val p: MinePresenter
    ) : SongListItemBinder.OnItemClick {
        override fun onItemClick(position: Int) {
            val item = items[position] as SongListItemObject
            MusicLog.i(TAG, "SongListClickListener/ item=$item")
            p.getPlayListDetail(item.songId)
        }
    }
}
