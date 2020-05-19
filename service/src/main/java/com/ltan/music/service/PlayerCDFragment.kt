package com.ltan.music.service

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import butterknife.BindView
import com.bumptech.glide.Glide
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrl
import com.ltan.music.common.MusicLog
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.SongUtils
import com.ltan.music.service.adapter.CdClickListener
import com.ltan.music.service.contract.ServiceContract
import com.ltan.music.service.presenter.ServicePresenter
import com.ltan.music.widget.constants.PlayListItemPreview

/**
 * TMusic.com.ltan.music.service
 *
 * @ClassName: PlayerCDFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-23
 * @Version: 1.0
 */
class PlayerCDFragment : BaseMVPFragment<ServicePresenter>(), ServiceContract.View {

    companion object {
        private const val TAG = "PlayerCDFragment"
        const val ARGS_SONG = "args_song"
        fun newInstance(): PlayerCDFragment {
            return PlayerCDFragment()
        }
    }

    @BindView(R2.id.fl_cd_singer_preview)
    lateinit var mSingerBgFl: FrameLayout
    @BindView(R2.id.crliv_song_alb)
    lateinit var mSongAlbumIv: ImageView
    private var mClickListener: CdClickListener? = null

    private var mRotateAnim: Animator? = null
    private var mInitialized: Boolean = false
    val initialized: Boolean get() = mInitialized

    private lateinit var mCurrentSong: SongItemObject

    override fun initLayout(): Int {
        return R.layout.service_player_cd
    }

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processArgs()
    }

    override fun init(view: View) {
        super.init(view)
        initView(view)
        initAnim()
        // indicate anim initialized
        mInitialized = true
    }

    override fun onResume() {
        super.onResume()
        showPreviewImage(mCurrentSong.picUrl)
    }

    override fun onSongDetail(songDetails: SongDetailRsp?) {
        MusicLog.d(TAG, "onSongDetail: privileges${songDetails?.privileges}\ntracks: ${songDetails?.tracks}")
        val tracks = songDetails?.tracks
        if (tracks == null || tracks.isNullOrEmpty()) {
            return
        }
        for (song in tracks) {
            if (song.id == mCurrentSong.songId) {
                mCurrentSong.picUrl = song.al?.picUrl
                showPreviewImage(song.al?.picUrl)
                break
            }
        }
    }

    override fun onSongUrl(songs: List<SongUrl>?) {
        MusicLog.d(TAG, "onSongUrl http returned $songs")
        mCurrentSong.songUrl = songs?.get(0)?.url
    }

    fun setCdClickListener(clickListener: CdClickListener?) {
        mClickListener = clickListener
    }

    fun cancelCDAnim() {
        val animator = mRotateAnim ?: return
        animator.cancel()
    }

    fun rotateCD(playing: Boolean) {
        val animator = mRotateAnim ?: return
        if (playing) {
            if (animator.isStarted || animator.isPaused) {
                animator.resume()
            } else {
                animator.start()
            }
        } else {
            animator.pause()
        }
    }

    private fun querySongDetail(ids: String, collector: String) {
        mPresenter.getSongDetail(ids, collector)
    }

    private fun querySongUrls(ids: String) {
        mPresenter.getSongUrl(ids)
    }

    private fun processArgs() {
        val args = arguments
        if (args == null || args.isEmpty) {
            return
        }
        mCurrentSong = args.getParcelable(ARGS_SONG) ?: throw IllegalArgumentException("Must process a SongItemObject")
        MusicLog.v(TAG, "mCurrentSong : $mCurrentSong")
    }

    private fun initView(root: View) {
        mCurrentSong.picUrl?.let {
            showPreviewImage(it)
        }
        if (mCurrentSong.picUrl == null) {
            val id = mCurrentSong.songId
            querySongDetail(SongUtils.buildArgs(id), SongUtils.buildCollectors(id))
            querySongUrls(SongUtils.buildArgs(id))
        }
        mSingerBgFl.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                val longClick = mClickListener
                if (longClick != null) {
                    return longClick.onLongClick()
                }
                return false
            }
        })
        mSingerBgFl.setOnClickListener {
            mClickListener?.onClick()
        }
    }

    private fun initAnim() {
        val valueAnimator = ObjectAnimator.ofFloat(mSingerBgFl, "rotation", 0F, 360F)
        valueAnimator.repeatCount = ObjectAnimator.INFINITE
        valueAnimator.repeatMode = ObjectAnimator.RESTART
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 10000

        // valueAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
        //     val value = it.animatedValue as Float
        //     mSingerBgFl.rotation = value
        // })
        mRotateAnim = valueAnimator
    }

    private fun showPreviewImage(picUrl: String?) {
        if (picUrl.isNullOrEmpty()) {
            return
        }
        Glide.with(requireContext().applicationContext)
            .load(picUrl)
            .error(PlayListItemPreview.ERROR_IMG)
            .placeholder(PlayListItemPreview.PLACEHOLDER_IMG)
            .into(mSongAlbumIv)
    }
}