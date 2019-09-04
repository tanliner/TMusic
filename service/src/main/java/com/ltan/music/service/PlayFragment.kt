package com.ltan.music.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrl
import com.ltan.music.business.bean.Track
import com.ltan.music.common.EasyBlur
import com.ltan.music.common.LyricsObj
import com.ltan.music.common.MusicLog
import com.ltan.music.common.StatusBar
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.SongUtils
import com.ltan.music.service.adapter.CdClickListener
import com.ltan.music.service.adapter.PlayerPageAdapter
import com.ltan.music.service.contract.ServiceContract
import com.ltan.music.service.presenter.ServicePresenter
import com.ltan.music.service.provider.MusicProvider
import com.ltan.music.service.widget.PlayerPageController
import com.ltan.music.widget.constants.PlayListItemPreview
import jp.wasabeef.glide.transformations.BlurTransformation
import kotterknife.bindView

/**
 * TMusic.com.ltan.music.service
 * playing with a CD-image
 *
 * @ClassName: PlayFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-08
 * @Version: 1.0
 */
class PlayFragment : BaseMVPFragment<ServicePresenter>(), ServiceContract.View {
    companion object {
        fun newInstance(): PlayFragment {
            return PlayFragment()
        }

        private const val TAG = "PlayFragment"
        const val MSG_UPDATE_PREVIEW = 0x1000
        const val MSG_UPDATE_SEEKBAR_PROGRESS = 0x1001
        const val MSG_PLAY_CD_ANIM = 0x1002
        const val FOCUS_COLOR = Color.GREEN
        const val DEFAULT_COLOR = Color.WHITE
    }

    interface ResourceReady {
        fun onFailed() {} /* default method */
        fun onReady()
    }

    private lateinit var mCurSong: SongPlaying
    private var mLastSongId = -1L
    private lateinit var mSongList: ArrayList<SongItemObject>
    private val mPreviewBg: ImageView by bindView(R.id.iv_play_service_bg)
    private val mNavIcon: ImageView by bindView(R.id.iv_play_service_back)
    private val mCDWhiteBg: ImageView by bindView(R.id.iv_cd_white_bg)
    private val mSongName: TextView by bindView(R.id.tv_play_service_song_name)
    private val mSongArtist: TextView by bindView(R.id.tv_play_service_song_artists)
    private val mSongPager: ViewPager by bindView(R.id.vp_song_playing)
    // todo slow down viewpager swipe
    // private val mSongPager: CdViewPager by bindView(R.id.vp_song_playing)
    // private val mPagerContainer: LinearLayout by bindView(R.id.ll_pager_container)
    private val mPagerContainer: RelativeLayout by bindView(R.id.rl_pager_container)
    private val mSongLyricSv: ScrollView by bindView(R.id.scroll_lyric)
    private val mLyricContainerRoot: LinearLayout by bindView(R.id.ll_song_text_container_root)
    private val mLyricContainer: LinearLayout by bindView(R.id.ll_song_text_container)
    private val mPlayerPageController: PlayerPageController by bindView(R.id.service_pager_controller)
    private val mPlayerSticker: ImageView by bindView(R.id.iv_cd_sticker)

    private lateinit var adapter: PlayerPageAdapter
    private var mCurrentSongDetail: Track? = null

    private lateinit var appCtx: Context
    private var mServiceConn = PlayerConnection()
    private var mCurrentSelectId = 0L
    private var mMusicBinder: MusicService.MyBinder? = null
    private lateinit var mLyricHighLight: ScrollLyricHighLight
    private val mHandler = MyHandler()
    // lyric of current song playing
    private var mLyric: LyricsObj? = null

    private lateinit var mPageChangeListener: PagerChangeListener
    private lateinit var mStickerAnimIn: Animation
    private lateinit var mStickerAnimOut: Animation

    @SuppressLint("HandlerLeak")
    inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                // MSG_UPDATE_PREVIEW -> updateCurrentBg(msg.obj as SongItemObject)
                MSG_UPDATE_PREVIEW -> onPageSelected(msg.obj as SongItemObject)
                MSG_UPDATE_SEEKBAR_PROGRESS -> updateCurrentPos()
                MSG_PLAY_CD_ANIM -> playCDAnim()
            }
        }
    }

    override fun initLayout(): Int {
        return R.layout.service_player
    }

    private fun bindService(songUrl: String?) {
        val ctx = requireContext()
        val intent = Intent(ctx, MusicService::class.java)
        intent.putExtra("songUrl", songUrl)
        ctx.bindService(intent, mServiceConn, Service.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appCtx = context!!.applicationContext
    }

    override fun init(view: View) {
        super.init(view)
        processArgs()

        initView()
        setLayoutBg(mCurSong.picUrl)
        bindService(mCurSong.url)
        loadAnims()
        MusicProvider()
    }

    private fun processArgs() {
        val args = arguments ?: return
        mCurSong = args.get(PlayerActivity.ARG_OBJ) as SongPlaying
        // make sure not null
        val songList: ArrayList<SongItemObject> = args.getParcelableArrayList(PlayerActivity.ARG_SONG_LIST)
        mSongList = if (songList.isNullOrEmpty()) {
            ArrayList()
        } else {
            songList
        }
        // mSongList = args.getParcelableArrayList(PlayerActivity.ARG_SONG_LIST)
        mLastSongId = mCurSong.id
    }

    private fun initView() {
        val mlp = mNavIcon.layoutParams as RelativeLayout.LayoutParams
        mlp.topMargin = StatusBar.getStatusBarHeight(this.requireContext())
        mNavIcon.layoutParams = mlp

        mNavIcon.setOnClickListener { onBack() }
        mSongName.text = mCurSong.title
        mSongArtist.text = appendSpace(mCurSong.artists)

        adapter = PlayerPageAdapter(childFragmentManager)
        mSongPager.adapter = adapter
        // TODO slow down viewpager swipe
        // mSongPager.setDurationScroll(1)
        adapter.setSongs(mSongList)
        var index = 0
        for (i in 0 until mSongList.size) {
            if (mSongList[i].songId == mCurSong.id) {
                index = i
                break
            }
        }
        mSongPager.setCurrentItem(index, true)
        mPageChangeListener = PagerChangeListener(mSongList)
        mSongPager.addOnPageChangeListener(mPageChangeListener)
        mPlayerPageController.setDataSource(mSongList)

        adapter.setOnClickListener(object : CdClickListener {
            override fun onLongClick(): Boolean {
                MusicLog.d(TAG, "onLongClick show original image")
                return true
            }

            override fun onClick() {
                showLyric()
            }
        })
        mPlayerPageController.setOnClickListener { showLyric() }
        mSongLyricSv.setOnClickListener { showLyric() }
        mLyricContainerRoot.setOnClickListener { showLyric() }
        mPagerContainer.setOnClickListener { showLyric() }
    }

    private fun loadAnims() {
        mStickerAnimIn = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_cd_sticker_in)
        mStickerAnimOut = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_cd_sticker_out)
    }

    private fun playStickerAnim(anim: Animation) {
        val lastAnim = mPlayerSticker.animation
        if (lastAnim != null && !lastAnim.hasEnded()) {
            lastAnim.cancel()
        }
        mPlayerSticker.animation = anim
        mPlayerSticker.startAnimation(anim)
        // anim.start()
    }

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    override fun onSongUrl(songs: List<SongUrl>?) {
        if (songs.isNullOrEmpty()) {
            return
        }
        for (i in 0 until songs.size) {
            if (mCurrentSelectId == songs[i].id) {
                songs[i].url?.let { mCurSong.url = it }
                break
            }
        }

        updateServiceSong()
        mMusicBinder?.play(mCurSong)
    }

    override fun onSongDetail(songDetails: SongDetailRsp?) {
        val tracks = songDetails?.tracks
        if (tracks == null || tracks.isNullOrEmpty()) {
            return
        }
        mCurrentSongDetail = tracks[0]
        mCurrentSongDetail?.let {
            MusicLog.d(TAG, "onSongDetail pic for ${mCurSong.title}: ${it.al?.picUrl}")
            setLayoutBg(it.al?.picUrl)
            mCurSong.picUrl = it.al?.picUrl
            updateServiceSong()
        }
    }

    override fun onResume() {
        super.onResume()
        mMusicBinder?.let {
            mPlayerPageController.setState(it.isPlaying)
            if (it.isPlaying) {
                updateCurrentPos()
                playCDAnim()
            }
            it.addCallback(mServiceConn.playerCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeMessages(MSG_UPDATE_SEEKBAR_PROGRESS)
        mHandler.removeMessages(MSG_UPDATE_PREVIEW)
        mMusicBinder?.removeCallback(mServiceConn.playerCallback)
        mLastSongId = mCurSong.id
        mMusicBinder?.let { mCurSong = it.getCurrentSong() }
        pauseCDAnim()
    }

    private fun playCDAnim() {
        val fragment = adapter.getCurrentItem(mSongPager)
        if (fragment is PlayerCDFragment) {
            if (fragment.initialized) {
                mHandler.removeMessages(MSG_PLAY_CD_ANIM)
                fragment.rotateCD(true)
            } else {
                val msg = mHandler.obtainMessage(MSG_PLAY_CD_ANIM)
                mHandler.sendMessageDelayed(msg, 1000);
            }
        }
    }

    private fun pauseCDAnim() {
        val fragment = adapter.getCurrentItem(mSongPager)
        if (fragment is PlayerCDFragment) {
            fragment.rotateCD(false)
        }
    }

    private fun cancelCDAnim() {
        val fragment = adapter.getCurrentItem(mSongPager)
        if (fragment is PlayerCDFragment) {
            fragment.cancelCDAnim()
        }
    }

    private fun updateServiceSong() {
        val song = mMusicBinder?.getCurrentSong() ?: return
        val curSong = mCurSong

        song.id = curSong.id
        song.url = curSong.url
        song.title = curSong.title
        song.subtitle = curSong.subtitle
        song.artists = curSong.artists
        song.picUrl = curSong.picUrl
        song.lyrics = curSong.lyrics
    }

    private fun appendSpace(artist: String): String {
        return StringBuilder().append(artist).append(' ').toString()
    }

    private fun updateTitle(title: String? = "", artist: String? = "") {
        val pos = mMusicBinder?.getCurrentIndex() ?: return // binder is null
        val curItem = mSongList.get(pos) ?: return         // item is null
        updateTitle(curItem)
    }

    private fun updateTitle(item: SongItemObject) {
        mSongName.text = item.title
        mSongArtist.text = item.artists
    }

    private fun onPageSelected(item: SongItemObject) {
        updateTitle(item)
        updateCurrentSong(item)
        updateServiceSong()
        prepareSongPlaying(item)
    }

    /**
     * transfer [SongItemObject] into [SongPlaying] used by [MusicService]
     */
    private fun updateCurrentSong(item: SongItemObject) {
        // update current playing song, and sync to service
        mCurrentSelectId = item.songId
        mCurSong.id = item.songId
        mCurSong.title = item.title
        mCurSong.subtitle = item.subTitle
        mCurSong.artists = item.artists
        mCurSong.picUrl = item.picUrl
        mCurSong.lyrics = item.lyrics
        item.songUrl?.let { mCurSong.url = it }
    }

    private fun prepareSongPlaying(song: SongItemObject) {
        // just replay if picUrl not empty, otherwise to query the song detail and song-url
        if (song.picUrl != null) {
            setLayoutBg(song.picUrl)
            mMusicBinder?.play(mCurSong)
        } else {
            mPresenter.getSongDetail(SongUtils.buildArgs(song.songId), SongUtils.buildCollectors(song.songId))
            mPresenter.getSongUrl(SongUtils.buildArgs(song.songId))
        }
    }

    private fun setLayoutBg(picUrl: String?) {
        Glide.with(appCtx)
            .load(picUrl)
            .error(PlayListItemPreview.ERROR_IMG)
            .placeholder(PlayListItemPreview.PLACEHOLDER_IMG)
            .listener(DefRequestListener(ImageBlur(appCtx, mPreviewBg)))
            .transform(BlurTransformation(20, 25)) /* fast blur in Java layer */
            .transform(BlurTransformation(20, 26))
            .into(mPreviewBg)
    }

    private fun updateCurrentPos() {
        val binder = mMusicBinder ?: return
        mPlayerPageController.updateCur(binder.currentPosition.toLong())
        mHandler.removeMessages(MSG_UPDATE_SEEKBAR_PROGRESS)
        val msg = mHandler.obtainMessage(MSG_UPDATE_SEEKBAR_PROGRESS)
        mHandler.sendMessageDelayed(msg, 1000)
    }

    private fun showLyric() {
        val isShowingCD = mPagerContainer.visibility == View.VISIBLE
        if (isShowingCD) {
            mPagerContainer.visibility = View.GONE
            mCDWhiteBg.visibility = View.GONE
            mSongLyricSv.visibility = View.VISIBLE
            mSongLyricSv.post {
                generateLyric()
            }
        } else {
            mPagerContainer.visibility = View.VISIBLE
            mCDWhiteBg.visibility = View.VISIBLE
            mSongLyricSv.visibility = View.GONE
        }
    }

    /**
     * Get lyric from service
     * new TextView into lyric container
     *
     * Space
     * line -- text
     * line -- text
     * ...
     * Space
     */
    private fun generateLyric() {
        if (mLyricContainer.visibility != View.VISIBLE) {
            return
        }
        if (mLyricContainer.childCount > 0 && mCurSong.id == mLastSongId) {
            // same song
            return
        }
        // todo recycle view
        mLastSongId = mCurSong.id
        mLyricContainer.removeAllViews()

        mLyric?.songTexts?.let {
            for (lineObj in it) {
                val line = lineObj.txt
                val start = lineObj.start

                val itemView = generateLyricItem(line)
                mLyricContainer.addView(itemView)
            }
        }
    }

    /**
     * [line] lyric text
     * [TextView] return the lyric ItemView
     */
    private fun generateLyricItem(line: String): TextView {
        val tv = TextView(activity)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (line.isEmpty()) {
            // lp.height = 0
        }
        tv.setPadding(0, 20, 0, 20)
        tv.gravity = Gravity.CENTER
        tv.text = line
        tv.textSize = 16.0F
        tv.setTextColor(Color.WHITE)
        tv.layoutParams = lp
        return tv
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unbindService(mServiceConn)
    }

    /**
     * Blur the whole layout background when resource ready
     */
    class ImageBlur constructor(appCtx: Context, target: ImageView) : ResourceReady {
        private val ctx = appCtx
        private val mPreviewBg = target

        override fun onReady() {
            mPreviewBg.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    mPreviewBg.isDrawingCacheEnabled = true
                    val bitmap = mPreviewBg.drawingCache
                    mPreviewBg.viewTreeObserver.removeOnPreDrawListener(this)
                    val result = EasyBlur.sInstance
                        .with(ctx)
                        .bitmap(bitmap)
                        .radius(25)
                        .scale(45)
                        .blur()
                    // val result2 = FastBlur.blur(bitmap, 30, true)
                    // mPreviewBg.setImageBitmap(result2)
                    mPreviewBg.setImageBitmap(result)
                    mPreviewBg.isDrawingCacheEnabled = false
                    return false
                }
            })
        }
    }

    /**
     * Ignore the params, just use the call back
     */
    open class DefRequestListener(cb: ResourceReady) : RequestListener<Drawable> {
        private val callback: ResourceReady = cb
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            callback.onFailed()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            callback.onReady()
            return false
        }
    }


    inner class PagerChangeListener(songList: ArrayList<SongItemObject>) : ViewPager.OnPageChangeListener {
        private val mSongList = songList
        private var mLastState = 0
        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE && mLastState == ViewPager.SCROLL_STATE_SETTLING) {
                playStickerAnim(mStickerAnimIn)
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                playStickerAnim(mStickerAnimOut)
            }
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            }
            mLastState = state
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            val curItem = mSongList[position]

            // pause media player
            mMusicBinder?.pause()
            mMusicBinder?.setCurrentIndex(position)
            mLyricContainer.removeAllViews()
            mLyric = null
            mLastSongId = -1
            mHandler.removeMessages(MSG_UPDATE_PREVIEW)
            val msg = mHandler.obtainMessage(MSG_UPDATE_PREVIEW)
            msg.obj = curItem
            mHandler.sendMessageDelayed(msg, 500)
        }
    }

    inner class PlayerConnection : ServiceConnection {
        lateinit var playerCallback: PlayerCallbackImpl
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val musicBinder = service as MusicService.MyBinder
            playerCallback = PlayerCallbackImpl(mPlayerPageController)

            mLyricHighLight = ScrollLyricHighLight()
            playerCallback.setLyricHighLight(mLyricHighLight)
            playerCallback.setViewPagerUpdate(ViewPagerUpdateImpl())

            mMusicBinder = musicBinder
            musicBinder.addCallback(playerCallback)
            // update the index recycler-view, the 'Next-Song' button will change it
            musicBinder.setCurrentIndex(mSongPager.currentItem)
            // set the current lyric
            mLyric = musicBinder.getLyric()
            // update current media service state
            mPlayerPageController.setState(musicBinder.isPlaying)
            mPlayerPageController.setMediaPlayer(musicBinder)
            // update seekbar position
            if (musicBinder.isPlaying) {
                updateCurrentPos()
                playCDAnim()
            }
        }
    }

    inner class ScrollLyricHighLight : LyricHighLight {
        private var mLyricLastIndex = -10
        override fun onHighLight(txt: String?, index: Int) {
            if (mLyricContainer.childCount <= 0) {
                return
            }
            var realIndex = index
            if (TextUtils.isEmpty(mMusicBinder?.getLyric()?.songTexts?.get(index)?.txt)) {
                realIndex = index - 1
                if (realIndex < 0) {
                    realIndex = 0
                }
            }
            updateHighlight(realIndex)
        }

        /**
         * highlight the lyric line, and scroll-up with 100ms, make lyric center-vertical
         * Note: Please run on UI
         */
        private fun updateHighlight(index: Int) {
            val container = mLyricContainer
            mHandler.post {
                for (i in 0 until container.childCount) {
                    val child = container.getChildAt(i) as TextView
                    if (index == i) {
                        child.setTextColor(FOCUS_COLOR)
                    } else {
                        child.setTextColor(DEFAULT_COLOR)
                    }
                }
            }
            mLyricLastIndex = index
            // delay to scroll all lyric lines
            mHandler.postDelayed({
                // NPE if switch to next song, and lyric line not the first one
                val lyricItem = container.getChildAt(index) ?: return@postDelayed
                val itemHeight = lyricItem.measuredHeight
                mSongLyricSv.smoothScrollTo(0, (index - 6) * itemHeight)
            }, 100L)
        }

        override fun onStart() {
            val binder = mMusicBinder ?: return
            mCurSong = binder.getCurrentSong()
            if (mLyricLastIndex == 0) {
                mSongLyricSv.smoothScrollTo(0, 0)
            }
            mPlayerPageController.updateMax(binder.duration)
            mHandler.obtainMessage(MSG_UPDATE_SEEKBAR_PROGRESS).sendToTarget()
            playCDAnim()
        }

        override fun onPause() {
            mHandler.removeMessages(MSG_UPDATE_SEEKBAR_PROGRESS)
            pauseCDAnim()
        }

        override fun onComplete() {
            mLyricLastIndex = 0
            mHandler.removeMessages(MSG_UPDATE_SEEKBAR_PROGRESS)
            cancelCDAnim()
        }

        override fun onLyric(lyric: LyricsObj?) {
            MusicLog.w(TAG, "wtf onLyric ===== $lyric\n${mSongLyricSv.visibility}")
            mLyric = lyric
            if (mSongLyricSv.visibility == View.VISIBLE) {
                mHandler.post { generateLyric() }
            }
        }

        override fun onSongChange(title: String, subtitle: String) {
            mHandler.post {
                updateTitle(title, subtitle)
            }
        }

        override fun onSongPreviewUpdate(url: String?) {
            val binder = mMusicBinder ?: return
            val curIndex = mSongPager.currentItem
            val targetIndex = binder.getCurrentIndex()
            MusicLog.d(TAG, "onSongPreviewUpdate swipe index. $curIndex vs $targetIndex")
            mHandler.post { setLayoutBg(url) }
        }
    }

    inner class ViewPagerUpdateImpl : IViewPagerUpdate {
        override fun onNext(index: Int, curSong: SongPlaying) {
            val curIndex = mSongPager.currentItem
            mCurSong = curSong
            mHandler.post {
                swipeViewPager(curIndex, index)
            }
        }

        override fun onLast(index: Int, curSong: SongPlaying) {
            val curIndex = mSongPager.currentItem
            mCurSong = curSong
            mHandler.post {
                swipeViewPager(curIndex, index)
            }
        }

        private fun swipeViewPager(curIndex: Int, targetIndex: Int) {
            mSongPager.removeOnPageChangeListener(mPageChangeListener)
            if (curIndex != targetIndex) {
                mHandler.post {
                    mSongPager.setCurrentItem(targetIndex, true)
                    mSongPager.addOnPageChangeListener(mPageChangeListener)
                }
            }
        }
    }
}