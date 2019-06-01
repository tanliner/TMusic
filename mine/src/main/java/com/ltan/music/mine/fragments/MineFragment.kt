package com.ltan.music.mine.fragments

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.account.utils.AccountUtil
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.common.MusicLog
import com.ltan.music.common.ToastUtil
import com.ltan.music.mine.*
import com.ltan.music.mine.adapter.*
import com.ltan.music.mine.beans.PlayList
import com.ltan.music.mine.beans.SongSubCunt
import com.ltan.music.mine.contract.IMineContract
import com.ltan.music.mine.presenter.MinePresenter
import com.ltan.music.service.MusicService
import com.ltan.music.widget.ClickType
import com.ltan.music.widget.ListItemClickListener
import com.ltan.music.widget.MusicPlayerController
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

    private lateinit var mMultiAdapter: MultiTypeAdapter
    private lateinit var mRcyItems: MutableList<Any>

    // local, recent, download, FM, collection
    private lateinit var mStableList: ArrayList<CollectorItemObject>
    // created song list, collected song list
    private lateinit var mSongCategoryList: ArrayList<SongListCategoryObject>

    private val mRclView: RecyclerView by bindView(R.id.rclv_mine)
    private val mControllerView: MusicPlayerController by bindView(R.id.mmp_controller)
    // collection count
    private var mArtistCount = 0
    // song list / subscribe song list
    private var mCreatedSongListCount = 0
    private var mSubSongListCount = 0

    private var mCreatedCategory = ArrayList<SongListItemObject>()
    private var mSubCategory = ArrayList<SongListItemObject>()
    private var mServiceConn = PlayerConnection()
    private var mMusicBinder: MusicService.MyBinder? = null

    private var mPlayerCallback: PlayerCallbackImpl? = null

    override fun initLayout(): Int {
        return R.layout.mine_fragment
    }

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    override fun init(view: View) {
        super.init(view)
        mRclView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mMultiAdapter = MultiTypeAdapter()
        // recycle
        mMultiAdapter.register(String::class, MineHeaderBinder(requireContext(), generateHeaderItems()))
        mMultiAdapter.register(CollectorItemObject::class.java, CollectorItemBinder(requireContext()))
        mMultiAdapter.register(HorizontalLine::class.java, HorizontalLineBinder())
        val categoryBinder = SongListCategoryBinder()
        mMultiAdapter.register(SongListCategoryObject::class.java, categoryBinder)
        val songListBinder = SongListItemBinder(requireContext())
        mMultiAdapter.register(SongListItemObject::class.java, songListBinder)
        mMultiAdapter.register(PlaceItem::class.java, PlaceItemBinder())
        genItems()
        mMultiAdapter.items = mRcyItems

        categoryBinder.setOnItemClick(CategoryClickListener(mRcyItems, mCreatedCategory, mSubCategory, mMultiAdapter))
        songListBinder.setOnItemClick(SongListClickListener(this, mRcyItems))

        mRclView.adapter = mMultiAdapter
        // http request, maybe after resume
        mPresenter.subcount()
        startService()
    }

    private fun startService() {
        val intent = Intent(context, MusicService::class.java)
        context?.startService(intent)
        context?.bindService(intent, mServiceConn, Service.BIND_AUTO_CREATE)
    }

    private fun stopService() {
        val intent = Intent(context, MusicService::class.java)
        context?.stopService(intent)
        context?.unbindService(mServiceConn)
    }

    override fun onResume() {
        super.onResume()
        if(mMusicBinder != null) {
            val curSong = mMusicBinder!!.getCurrentSong()
            mPlayerCallback?.let { mMusicBinder!!.addCallback(it) }
            if(mMusicBinder!!.isPlaying) {
                mControllerView.updateTitle(curSong.title)
                mControllerView.setState(true)
            } else {
                mControllerView.setState(false)
                mControllerView.updateDisplay(curSong.title, curSong.subtitle)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(mMusicBinder != null) {
            mPlayerCallback?.let { mMusicBinder!!.removeCallback(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    override fun onSubcount(data: SongSubCunt?) {
        if(data == null) {
            ToastUtil.showToastShort(getString(R.string.mine_song_list_failed))
            return
        }
        MusicLog.d(TAG, "data is: $data")
        mArtistCount = data.artistCount
        mCreatedSongListCount = data.createdPlaylistCount
        mSubSongListCount = data.subPlaylistCount

        mStableList[4].count = data.artistCount
        mSongCategoryList[0].count = data.createdPlaylistCount
        mSongCategoryList[1].count = data.subPlaylistCount
        mMultiAdapter.notifyDataSetChanged()
        // mMultiAdapter.notifyItemRangeChanged(5, 4)
        AccountUtil.getAccountInfo()?.let { mPresenter.getPlayList(it.id) }
    }

    override fun onPlayList(data: List<PlayList>?) {
        if(data == null) {
            ToastUtil.showToastShort(getString(R.string.mine_play_list_failed))
            return
        }
        MusicLog.d(TAG, "play list is: $data")
        val imgId = R.drawable.page_header_item_search

        // category created
        for (i in 0 until min(mCreatedSongListCount, data.size)) {
            val playItem = data[i]
            val item = SongListItemObject(playItem.id, 0, playItem.coverImgUrl, playItem.name, playItem.trackCount)
            item.owner = playItem.creator?.nickname
            mCreatedCategory.add(item)
        }
        if(mCreatedSongListCount > 0) {
            mCreatedCategory[0].isHeartMode = true
        }
        // category subscribed
        for (i in mCreatedSongListCount until data.size) {
            val playItem = data[i]
            val item = SongListItemObject(playItem.id, 0, playItem.coverImgUrl, playItem.name, playItem.trackCount)
            item.owner = playItem.creator?.nickname
            mSubCategory.add(item)
        }
        var favoriteOffset = CATEGORY_LIST_INDEX + 1
        if(mSongCategoryList[0].state == State.EXPAND) {
            favoriteOffset += mCreatedSongListCount
            mRcyItems.addAll(CATEGORY_LIST_INDEX, mCreatedCategory)
        }
        if(mSongCategoryList[1].state == State.EXPAND) {
            mRcyItems.addAll(favoriteOffset, mSubCategory)
        }
        mMultiAdapter.notifyDataSetChanged()
        // mMultiAdapter.notifyItemRangeInserted(CATEGORY_LIST_INDEX, mCreatedSongListCount + mSubSongListCount)
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
        mStableList = genCollectorItems()
        mSongCategoryList = getSongCategories()

        mRcyItems = ArrayList()
        mRcyItems.add("") // recycle view
        mRcyItems.addAll(mStableList)
        mRcyItems.add(HorizontalLine())
        mRcyItems.addAll(mSongCategoryList)
        mRcyItems.add(PlaceItem())
    }

    /**
     * create stable items
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
        category1.state = State.EXPAND
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
    ) : ListItemClickListener {
        override fun onItemClick(position: Int, v: View, type: ClickType) {
            if (type == ClickType.ITEM) {
                itemClick(position, v)
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
        private val frag: Fragment,
        private val items: MutableList<Any>
    ) : ListItemClickListener {
        override fun onItemClick(position: Int, v: View, type: ClickType) {
            if (type == ClickType.ITEM) {
                itemClick(position, v)
            } else {
                ToastUtil.showToastShort("$type")
            }
        }
        private fun itemClick(position: Int, view: View) {
            val item = items[position] as SongListItemObject
            MusicLog.d(TAG, "SongListClickListener/ item=$item")
            val intent = Intent(frag.context, SongListActivity::class.java)
            intent.putExtra(SongListActivity.ARG_SONG, item)
            frag.startActivity(intent)
        }
    }

    inner class PlayerConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mMusicBinder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            MusicLog.i(TAG, "service connected...")
            val binder = service as MusicService.MyBinder
            mMusicBinder = binder
            mPlayerCallback = PlayerCallbackImpl(mControllerView)
            mMusicBinder?.addCallback(mPlayerCallback!!)
            mControllerView.setPlayer(binder)
        }
    }
}
