package com.ltan.music.service

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
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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
import com.ltan.music.common.MusicLog
import com.ltan.music.common.StatusBar
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.ReqArgs
import com.ltan.music.service.adapter.CdClickListener
import com.ltan.music.service.adapter.PlayerPageAdapter
import com.ltan.music.service.contract.ServiceContract
import com.ltan.music.service.presenter.ServicePresenter
import com.ltan.music.service.widget.PlayerPageController
import com.ltan.music.widget.constants.PlayListItemPreview
import jp.wasabeef.glide.transformations.BlurTransformation
import kotterknife.bindView

/**
 * TMusic.com.ltan.music.service
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
        const val FOCUS_COLOR = Color.GREEN
        const val DEFAULT_COLOR = Color.WHITE
    }

    interface ResourceReady {
        fun onFailed() {} /* default method */
        fun onReady()
    }

    private lateinit var mCurSong: SongPlaying
    private lateinit var mInitSong: SongPlaying
    private var mSongList: ArrayList<SongItemObject>? = null
    private val mPreviewBg: ImageView by bindView(R.id.iv_play_service_bg)
    private val mNavIcon: ImageView by bindView(R.id.iv_play_service_back)
    private val mSongName: TextView by bindView(R.id.tv_play_service_song_name)
    private val mSongArtist: TextView by bindView(R.id.tv_play_service_song_artists)
    private val mSongPager: ViewPager by bindView(R.id.vp_song_playing)
    private val mPagerContainer: LinearLayout by bindView(R.id.ll_pager_container)
    private val mSongLyricSv: ScrollView by bindView(R.id.scroll_lyric)
    private val mLyricContainer: LinearLayout by bindView(R.id.ll_song_text_container)
    private val mPlayerPageController: PlayerPageController by bindView(R.id.service_pager_controller)

    private lateinit var adapter: PlayerPageAdapter
    private var mCurrentSongDetail: Track? = null

    private lateinit var appCtx: Context
    private var mServiceConn = PlayerConnection()
    private var mCurrentSelectId = 0L
    private var mMusicBinder: MusicService.MyBinder? = null
    private lateinit var mLyricHighLight: ScrollLyricHighLight

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
            MusicLog.i(TAG, "onSongDetail pic: ${it.al}")
            setLayoutBg(it.al?.picUrl)
            mCurSong.picUrl = it.al?.picUrl
            updateServiceSong()
        }
    }

    override fun onResume() {
        super.onResume()
        mMusicBinder?.let {
            mPlayerPageController.setState(it.isPlaying)
            it.addCallback(mServiceConn.playerCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        mMusicBinder?.removeCallback(mServiceConn.playerCallback)
        mInitSong = mCurSong
        mMusicBinder?.let { mCurSong = it.getCurrentSong() }
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
    }

    private fun processArgs() {
        val args = arguments ?: return
        mCurSong = args.get(PlayerActivity.ARG_OBJ) as SongPlaying
        mInitSong = mCurSong
        mSongList = args.getParcelableArrayList(PlayerActivity.ARG_SONG_LIST)
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
        mSongList?.let {
            adapter.setSongs(it)
            var index = 0
            for (i in 0 until it.size) {
                if (it[i].songId == mCurSong.id) {
                    index = i
                }
            }
            mSongPager.setCurrentItem(index, true)
            mSongPager.addOnPageChangeListener(PagerChangeListener(it))
            mPlayerPageController.setDataSource(it)
        }

        adapter.setOnClickListener(object : CdClickListener{
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
        mLyricContainer.setOnClickListener { showLyric() }
        mPagerContainer.setOnClickListener { showLyric() }
    }

    private fun appendSpace(artist: String): String {
        return StringBuilder().append(artist).append(' ').toString()
    }

    private fun setLayoutBg(picUrl: String?) {
        Glide.with(appCtx)
            .load(picUrl)
            .error(PlayListItemPreview.ERROR_IMG)
            .placeholder(PlayListItemPreview.PLACEHOLDER_IMG)
            .listener(DefRequestListener(ImageBlur(appCtx, mPreviewBg)))
            .transform(BlurTransformation(20, 25)) /* fast blur in Java layer */
            .into(mPreviewBg)
    }

    private fun showLyric() {
        val lyricShowing = mSongPager.visibility == View.VISIBLE
        if (lyricShowing) {
            mSongPager.visibility = View.GONE
            mSongLyricSv.visibility = View.VISIBLE
            mSongLyricSv.post {
                generateLyric()
            }
        } else {
            mSongPager.visibility = View.VISIBLE
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
        if(mLyricContainer.childCount > 2 && mCurSong.id == mInitSong.id) {
            // same song
            return
        }
        mLyricContainer.removeAllViews()
        val binder = mMusicBinder ?: return
        val lyrics = binder.getLyric() ?: return
        val texts = lyrics.songTexts ?: return

        val headerSpace = TextView(activity)
        val headerLP = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
        headerSpace.layoutParams = headerLP
        mLyricContainer.addView(headerSpace)

        for (lineObj in texts) {
            val line = lineObj.txt
            val start = lineObj.start

            val itemView = generateLyricItem(line)
            mLyricContainer.addView(itemView)
        }
        val footerSpace = TextView(activity)
        val footerLP = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
        footerSpace.layoutParams = footerLP
        mLyricContainer.addView(footerSpace)
    }

    /**
     * [line] lyric text
     * [TextView] return the lyric ItemView
     */
    private fun generateLyricItem(line: String): TextView {
        val tv = TextView(activity)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if(line.isEmpty()) {
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
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            val curItem = mSongList[position]
            Handler().postDelayed({
                mSongName.text = curItem.title
                mSongArtist.text = curItem.artists

                // update current playing song, and sync to service
                mCurrentSelectId = curItem.songId
                mCurSong.id = curItem.songId
                mCurSong.title = curItem.title
                mCurSong.picUrl = curItem.picUrl
                curItem.songUrl?.let { mCurSong.url = it }
                updateServiceSong()

                // just replay if picUrl not empty, otherwise to query the song detail and song-url
                if (curItem.picUrl != null) {
                    setLayoutBg(curItem.picUrl)
                    mMusicBinder?.play(mCurSong)
                } else {
                    mPresenter.getSongDetail(ReqArgs.buildArgs(curItem.songId), ReqArgs.buildCollectors(curItem.songId))
                    mPresenter.getSongUrl(ReqArgs.buildArgs(curItem.songId))
                }
            }, 500)
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

            mMusicBinder = musicBinder
            musicBinder.addCallback(playerCallback)
            // update current media service state
            mPlayerPageController.setState(musicBinder.isPlaying)
            mPlayerPageController.setMediaPlayer(musicBinder)
        }
    }

    inner class ScrollLyricHighLight : LyricHighLight {
        var mLyricLastIndex = -10
        override fun onHighLight(txt: String?, index: Int) {
            if(mLyricContainer.childCount <= 1) {
                return
            }
            val headerItemCount = 1
            var lastIndex = index - 1 + headerItemCount
            val lyricIndex = index + headerItemCount
            // last line is empty
            if (mLyricLastIndex == index - 2) {
                lastIndex = index - 1
            }
            mLyricLastIndex = index
            val curItem: TextView = mLyricContainer.getChildAt(lyricIndex) as TextView
            val lastItem: TextView = mLyricContainer.getChildAt(lastIndex) as TextView
            mLyricContainer.post {
                curItem.setTextColor(FOCUS_COLOR)
                lastItem.setTextColor(DEFAULT_COLOR)
            }
            mLyricContainer.postDelayed({
                mSongLyricSv.smoothScrollTo(0, (index - 4) * curItem.measuredHeight)
            }, 100L)
        }
    }
}