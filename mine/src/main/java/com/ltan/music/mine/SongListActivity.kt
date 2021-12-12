package com.ltan.music.mine

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.collection.LongSparseArray
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import com.ltan.music.basemvp.BaseMVPActivity
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrl
import com.ltan.music.business.bean.Track
import com.ltan.music.common.MusicLog
import com.ltan.music.common.StatusBar
import com.ltan.music.common.ToastUtil
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.SongUtils.buildArgs
import com.ltan.music.common.song.SongUtils.buildCollectors
import com.ltan.music.mine.adapter.PlaceItemBinder
import com.ltan.music.mine.adapter.SongItemBinder
import com.ltan.music.mine.adapter.SongListHeaderBinder
import com.ltan.music.mine.beans.PlayListDetailRsp
import com.ltan.music.mine.contract.SongListContract
import com.ltan.music.mine.presenter.SongListPresenter
import com.ltan.music.service.MusicService
import com.ltan.music.service.PlayerActivity
import com.ltan.music.service.SongPlaying
import com.ltan.music.widget.ClickType
import com.ltan.music.widget.ListItemClickListener
import com.ltan.music.widget.MusicPlayerController
import com.ltan.music.widget.MusicRecycleView
import com.ltan.music.widget.constants.PlayListItemPreview
import kotterknife.bindView
import me.drakeet.multitype.MultiTypeAdapter
import kotlin.math.min

/**
 * TMusic.com.ltan.music.mine
 * song-list
 *
 * @ClassName: SongListActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
class SongListActivity : BaseMVPActivity<SongListPresenter>(), SongListContract.View {

    companion object {
        const val ARG_SONG = "song_list"
    }

    override fun initLayout(): Int {
        return R.layout.mine_activity_song_list
    }

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    private var mCurrentSong = SongItemObject()
    private var mServiceConn = PlayerConnection()
    private var mMusicBinder: MusicService.MyBinder? = null
    private var mCurrentSongDetail: Track? = null

    private var mSongListId: Long = 0L
    private val mBackIcon: ImageView by bindView(R.id.iv_song_list_back)
    private val mSongListName: TextView by bindView(R.id.tv_song_list_name)

    private val mSongsRcyView: MusicRecycleView by bindView(R.id.rcy_mine_song_list)
    private val mSongListToolbar: LinearLayout by bindView(R.id.ll_song_list_toolbar)
    private val mFloatingToolbarBg: ImageView by bindView(R.id.iv_song_list_toolbar_bg)
    // floating item view
    private val mFloatingContainer: LinearLayout by bindView(R.id.ll_song_list_floating_play_all)
    private val mFloatingPlayAll: TextView by bindView(R.id.tv_song_list_play_all)
    private val mFloatingPlayAllCount: TextView by bindView(R.id.tv_song_list_play_all_count)

    private val mControllerView: MusicPlayerController by bindView(R.id.mmp_controller)
    private val mRcyAdapter = MultiTypeAdapter()

    // floating args
    private lateinit var songPlayListItem: SongListItemObject

    // used to find the SongItem by SongUrls
    private val indexMap = LongSparseArray<SongItemObject>()

    private lateinit var mRcyItems: MutableList<Any>
    private var mHeaderSize: Int = 0
    private var mFooterSize: Int = 0

    private lateinit var mDisplayMetrics: DisplayMetrics

    private var mDelayPlayingId: Long = 0

    private fun bindService(songUrl: String?) {
        val intent = Intent(baseContext, MusicService::class.java)
        intent.putExtra(MusicService.ARGS_SONG_URL, songUrl)
        bindService(intent, mServiceConn, Service.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        super.onCreate(savedInstanceState)
        processArgs()
        initView()
        querySongList()
        bindService(null)
    }

    private fun processArgs() {
        songPlayListItem = intent.getParcelableExtra(ARG_SONG) ?: return
        mSongListId = songPlayListItem.songId

        val header = SongListHeaderObject()
        header.previewUrl = songPlayListItem.imgUrl
        header.title = songPlayListItem.title
        header.songSize = songPlayListItem.count
        header.owner = songPlayListItem.owner
        header.extra = resources.getString(R.string.mine_song_list_header_edit_extra)

        mRcyItems = ArrayList()
        mRcyItems.add(header) // header button
        mRcyItems.add(PlaceItem()) // footer space
        mHeaderSize = 1
        mFooterSize = 1
    }

    private fun initView() {

        mSongListName.text = songPlayListItem.title
        mBackIcon.setOnClickListener { finish() }

        val songItemBinder = SongItemBinder()
        val headerBinder = SongListHeaderBinder()
        mRcyAdapter.register(SongListHeaderObject::class.java, headerBinder)
        mRcyAdapter.register(SongItemObject::class.java, songItemBinder)
        mRcyAdapter.register(PlaceItem::class.java, PlaceItemBinder())
        mRcyAdapter.items = mRcyItems

        songItemBinder.setOnItemClickListener(SongItemClick())
        mDisplayMetrics = resources.displayMetrics
        val lp = mSongListToolbar.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = StatusBar.getStatusBarHeight(this)
        mSongListToolbar.layoutParams = lp
        // paddingTop + previewImgHeight + marginTop - toolbarHeight
        val offset = (80 + 120 + 32 - 48) * mDisplayMetrics.density - lp.topMargin
        val titleOffset = (80 + 20) * mDisplayMetrics.density - lp.topMargin
        mSongListToolbar.setPadding(0, (0 * mDisplayMetrics.density).toInt(), 0, 0)
        // header alpha change
        headerBinder.setOffset(offset - titleOffset)
        headerBinder.setFloatingHeader(mFloatingToolbarBg)
        mSongsRcyView.addOnScrollListener(headerBinder)

        // floating toolbar update by scrollY
        mSongsRcyView.addOnScrollListener(object : MusicRecycleView.OnHeaderChangeListener {
            override fun onScrollChanged(scrollY: Int) {
                updateFloatingPlay(scrollY, offset, titleOffset)
                updateToolbarAlpha(scrollY, offset)
            }
        })
        initFloatingPlay(songPlayListItem)
        initFloatingToolbar()

        mSongsRcyView.layoutManager = LinearLayoutManager(this)
        mSongsRcyView.adapter = mRcyAdapter
    }

    /**
     * [titleOffset] show title when list scroll up
     * [offset] the floating play-all view
     */
    private fun updateFloatingPlay(scrollY: Int, offset: Float, titleOffset: Float) {
        if (scrollY >= offset) {
            mFloatingContainer.visibility = View.VISIBLE
        } else {
            mFloatingContainer.visibility = View.GONE
        }
        if (scrollY >= titleOffset) {
            mSongListName.text = songPlayListItem.title
        } else {
            mSongListName.text = resources.getString(R.string.mine_song_list_title)
        }
    }

    private fun updateToolbarAlpha(scrollY: Int, offset: Float) {
        mFloatingToolbarBg.visibility = View.VISIBLE
        val alpha = if (scrollY >= offset) 255 else (255 * scrollY / offset).toInt()
        mFloatingToolbarBg.drawable?.let {
            val mutable = it.mutate()
            mutable.alpha = alpha
            mFloatingToolbarBg.setImageDrawable(mutable)
        }
    }

    private fun initFloatingPlay(item: SongListItemObject) {
        mFloatingPlayAll.text = resources.getString(R.string.mine_song_list_header_play_all)
        mFloatingPlayAllCount.text = resources.getString(R.string.mine_song_list_header_song_count, item.count)
    }

    /**
     * floating header margin top is negative
     * let it below the view that both statusBar and toolbar
     */
    private fun initFloatingToolbar() {
        val mlp = mFloatingToolbarBg.layoutParams as RelativeLayout.LayoutParams
        mlp.topMargin = getTopMargin().toInt()
        mFloatingToolbarBg.layoutParams = mlp
    }

    private fun getTopMargin(): Float {
        val statusBarHeight: Int = StatusBar.getStatusBarHeight(this)
        // -rcyItemHeaderHeight + toolbarHeight + statusBarHeight
        return statusBarHeight +
                resources.getDimension(R.dimen.mine_song_list_toolbar_height) -
                resources.getDimension(R.dimen.mine_song_list_header_blur_bg)
    }

    private fun querySongList() {
        mPresenter.getPlayListDetail(mSongListId)
    }

    /**
     * to query the song url when song list is updated
     * [count] how many mp3 you want to load
     */
    private fun querySongUrls(count: Int) {
        if (mRcyItems.size > mHeaderSize) {
            val targetSize = min(mRcyItems.size - mFooterSize, count)
            val array = Array<Long>(targetSize) { 0 }
            for (i in mHeaderSize until targetSize) {
                val songItemObject = mRcyItems[i] as SongItemObject
                array[i] = songItemObject.songId
            }
            querySongUrls(buildArgs(array))
        }
    }

    /**
     * query all song urls, [ids] should me [10023, 200123, 302393, ...]
     */
    private fun querySongUrls(ids: String) {
        mPresenter.getSongUrl(ids)
    }

    private fun querySongDetail(ids: String, collector: String) {
        mPresenter.getSongDetail(ids, collector)
    }

    override fun onPlayListDetail(data: PlayListDetailRsp?) {
        if (data?.playlist == null) {
            ToastUtil.showToastShort(getString(R.string.mine_play_list_failed))
            return
        }

        MusicLog.d(TAG, "view onPlayListDetail: ${data.playlist}")
        val tracks = data.playlist.tracks
        if (tracks != null) {
            for (i in 0 until tracks.size) {
                val item = SongItemObject()
                item.songId = tracks[i].id
                item.number = (i + 1)
                item.title = tracks[i].name
                item.subTitle = buildSubtitle(tracks[i])
                item.artists = buildArtist(tracks[i])
                indexMap.put(item.songId, item)
                mRcyItems.add(mRcyItems.size - 1, item)
            }
        }
        mRcyAdapter.notifyDataSetChanged()
        // querySongUrls(5)
    }

    override fun onSongUrl(songs: List<SongUrl>?) {
        if (songs.isNullOrEmpty()) {
            return
        }
        for (i in 0 until songs.size) {
            val songItem = indexMap.get(songs[i].id)
            songItem?.songUrl = songs[i].url

            MusicLog.v(TAG, "url returned: ${mCurrentSong.songId} vs ${songs[i].id} ${songs[i].url}")
            if (mDelayPlayingId == songs[i].id) {
                songItem?.let { updateCurrentSong(it) }
                mCurrentSong.songUrl = songs[i].url
                val binder = mMusicBinder

                if(songs[i].url != null && binder != null) {
                    val song = binder.getCurrentSong()
                    updatePlayingSong(song)
                    mMusicBinder?.play(song)
                }
            }
        }
    }

    override fun onSongDetail(songDetails: SongDetailRsp?) {
        // todo AsyncTask callback when destroyed
        MusicLog.d(TAG, "onSongDetail: privileges${songDetails?.privileges}\ntracks: ${songDetails?.tracks}")
        val tracks = songDetails?.tracks
        if (tracks == null || tracks.isNullOrEmpty()) {
            return
        }
        for (song in tracks) {
            // update current song detail, usually preview picture
            if (mDelayPlayingId == song.id) {
                mCurrentSongDetail = song
                val picUrl = song.al?.picUrl ?: continue
                mCurrentSong.picUrl = picUrl
                indexMap[song.id]?.picUrl = picUrl
                updateControlPreview(picUrl)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mMusicBinder?.let {
            mControllerView.setState(it.isPlaying)
            it.addCallback(mServiceConn.playerCallback)
            mCurrentSong.picUrl = it.getCurrentSong().picUrl
        }
    }

    override fun onPause() {
        super.onPause()
        mMusicBinder?.removeCallback(mServiceConn.playerCallback)
    }

    private fun updatePlayingSong(playing: SongPlaying) {
        playing.id = mCurrentSong.songId
        mCurrentSong.songUrl?.let { playing.url = it }
        playing.title = mCurrentSong.title
        playing.subtitle = mCurrentSong.subTitle
        playing.artists =  mCurrentSong.artists
        playing.picUrl = mCurrentSong.picUrl
        playing.lyrics = mCurrentSong.lyrics
    }

    private fun buildSubtitle(tracks: Track): String {
        val sb = StringBuilder()
        sb.append(buildArtist(tracks))
        val album = tracks.al
        if (album != null) {
            sb.append(" - ").append(album.name)
        }
        return sb.toString()
    }

    private fun buildArtist(tracks: Track): String {
        val sb = StringBuilder()
        val ar = tracks.ar
        if (ar != null) {
            for (n in ar) {
                sb.append(n.name).append('/')
            }
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    private fun updateCurrentSong(song: SongItemObject) {
        mCurrentSong.songId = song.songId
        mCurrentSong.title = song.title
        mCurrentSong.subTitle = song.subTitle
        mCurrentSong.artists = song.artists
        mCurrentSong.lyrics = song.lyrics
    }

    /**
     * Update the bottom control view
     * ImagePreview song title, subtitle and so on
     */
    private fun updateControlPreview(picUrl: String?) {
        mMusicBinder?.let { it.getCurrentSong().picUrl = picUrl }
        Glide.with(this)
            .load(picUrl)
            .error(PlayListItemPreview.ERROR_IMG)
            .placeholder(PlayListItemPreview.PLACEHOLDER_IMG)
            .into(mControllerView.mPreviewIv)
    }

    /**
     * return the adapter data exclude header and footer
     */
    private fun getPlayingList(): ArrayList<SongItemObject> {
        val songItems = ArrayList<SongItemObject>()
        for (i in mHeaderSize until (mRcyItems.size - mFooterSize)) {
            val item = mRcyItems[i] as SongItemObject
            songItems.add(item)
        }
        return songItems
    }

    private fun goPlayerPage() {
        val binder = mMusicBinder ?: return
        val intent = Intent(this@SongListActivity, PlayerActivity::class.java)
        val songList = getPlayingList()
        intent.putExtra(PlayerActivity.ARG_OBJ, mMusicBinder?.getCurrentSong())
        intent.putParcelableArrayListExtra(PlayerActivity.ARG_SONG_LIST, songList)
        binder.setPlayingList(songList)
        startActivity(intent)
    }

    inner class SongItemClick : ListItemClickListener {
        private val TAG = "SongListAci/SongItemClick"

        override fun onItemClick(position: Int, v: View, type: ClickType) {
            MusicLog.v(TAG, "item click $position $v $type")
            val itemObject = mRcyItems[position] as SongItemObject
            updateCurrentSong(itemObject)
            mControllerView.updateDisplay(itemObject.title, itemObject.subTitle)

            val musicBinder = mMusicBinder ?: return
            // mRcyItems.size() = mHeaderSize + song.size() + mFooterSize
            musicBinder.setCurrentIndex(position - mHeaderSize)
            musicBinder.setPlayingList(getPlayingList())

            // query song detail, picUrl
            if (itemObject.picUrl.isNullOrEmpty()) {
                mControllerView.mPreviewIv.setImageResource(PlayListItemPreview.ALBUM_EMG)
                querySongDetail(buildArgs(itemObject.songId), buildCollectors(itemObject.songId))
            } else {
                mCurrentSongDetail = null
                updateControlPreview(itemObject.picUrl)
            }

            // query song url
            mCurrentSong.songId = itemObject.songId
            mCurrentSong.songUrl = itemObject.songUrl
            if (itemObject.songUrl.isNullOrEmpty()) {
                querySongUrls(buildArgs(itemObject.songId))
                // delay playing
                mDelayPlayingId = itemObject.songId
            } else {
                mDelayPlayingId = 0
                val song = musicBinder.getCurrentSong()
                if(itemObject.songId == song.id) {
                    if(!musicBinder.isPlaying) {
                        musicBinder.start()
                    }
                    goPlayerPage()
                } else {
                    updatePlayingSong(song)
                    musicBinder.play(song)
                }
            }
        }
    }

    inner class PlayerConnection : ServiceConnection {
        lateinit var playerCallback: PlayerCallbackImpl
        override fun onServiceDisconnected(name: ComponentName?) {
            mMusicBinder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            MusicLog.i(TAG, "service connected...")
            val binder = service as MusicService.MyBinder
            mMusicBinder = binder
            playerCallback = PlayerCallbackImpl(mControllerView)
            binder.addCallback(playerCallback)
            mControllerView.setPlayer(binder)
            mControllerView.setOnClickListener {
                goPlayerPage()
            }

            val curSong = binder.getCurrentSong()
            if (binder.isPlaying) {
                mControllerView.updateTitle(curSong.title)
                mControllerView.setState(true)
            } else if (curSong.id > 0) {
                mControllerView.updateDisplay(curSong.title, curSong.subtitle)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConn)
    }
}