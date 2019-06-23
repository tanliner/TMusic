package com.ltan.music.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.ltan.music.service.adapter.PlayerPageAdapter
import com.ltan.music.service.contract.ServiceContract
import com.ltan.music.service.presenter.ServicePresenter
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
    }

    interface ResourceReady {
        fun onFailed() {} /* default method */
        fun onReady()
    }

    private lateinit var mCurSong: SongPlaying
    private var mSongList: ArrayList<SongItemObject>? = null
    private val mPreviewBg: ImageView by bindView(R.id.iv_play_service_bg)
    private val mNavIcon: ImageView by bindView(R.id.iv_play_service_back)
    private val mSongName: TextView by bindView(R.id.tv_play_service_song_name)
    private val mSongArtist: TextView by bindView(R.id.tv_play_service_song_artists)
    private val mSongPager: ViewPager by bindView(R.id.vp_song_playing)

    private lateinit var adapter: PlayerPageAdapter
    private var mCurrentSongDetail: Track? = null

    override fun initLayout(): Int {
        return R.layout.service_player
    }

    lateinit var appCtx: Context
    private var mServiceConn = PlayerConnection()
    private var mCurrentSelectId = 0L
    private lateinit var mMusicBinder: MusicService.MyBinder

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
        mMusicBinder.play(mCurSong)
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

    private fun updateServiceSong() {
        val song = mMusicBinder.getCurrentSong()
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
        mSongList = args.getParcelableArrayList<SongItemObject>(PlayerActivity.ARG_SONG_LIST)
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
        }
        mSongList?.let { mSongPager.addOnPageChangeListener(PagerChangeListener(it)) }
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
                    mMusicBinder.play(mCurSong)
                } else {
                    mPresenter.getSongDetail(ReqArgs.buildArgs(curItem.songId), ReqArgs.buildCollectors(curItem.songId))
                    mPresenter.getSongUrl(ReqArgs.buildArgs(curItem.songId))
                }
            }, 500)
        }
    }

    inner class PlayerConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mMusicBinder = service as MusicService.MyBinder
        }
    }
}