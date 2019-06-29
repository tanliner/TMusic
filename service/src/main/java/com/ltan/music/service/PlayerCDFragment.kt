package com.ltan.music.service

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrl
import com.ltan.music.business.bean.Track
import com.ltan.music.common.MusicLog
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.common.song.ReqArgs
import com.ltan.music.service.adapter.CdClickListener
import com.ltan.music.service.contract.ServiceContract
import com.ltan.music.service.presenter.ServicePresenter
import com.ltan.music.widget.constants.PlayListItemPreview
import kotterknife.bindView

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

    private val mSingerBgFl: FrameLayout by bindView(R.id.fl_cd_singer_preview)
    private val mSongAlbumIv: ImageView by bindView(R.id.crliv_song_alb)
    private var mCurrentSongDetail: Track? = null
    private var mClickListener: CdClickListener? = null

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
    }

    override fun onResume() {
        super.onResume()
        showPreviewImage(mCurrentSongDetail?.al?.picUrl)
    }

    override fun onSongDetail(songDetails: SongDetailRsp?) {
        MusicLog.d(TAG, "onSongDetail: privileges${songDetails?.privileges}\ntracks: ${songDetails?.tracks}")
        val tracks = songDetails?.tracks
        if (tracks == null || tracks.isNullOrEmpty()) {
            return
        }
        mCurrentSongDetail = tracks[0]
        mCurrentSongDetail?.let {
            // update picUrl, the ImageBg need
            mCurrentSong.picUrl = it.al?.picUrl
            showPreviewImage(it.al?.picUrl)
        }
    }

    override fun onSongUrl(songs: List<SongUrl>?) {
        MusicLog.d(TAG, "onSongUrl returned $songs")
        mCurrentSong.songUrl = songs?.get(0)?.url
    }

    fun setCdClickListener(clickListener: CdClickListener?) {
        mClickListener = clickListener
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
            querySongDetail(ReqArgs.buildArgs(id), ReqArgs.buildCollectors(id))
            querySongUrls(ReqArgs.buildArgs(id))
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

    private fun showPreviewImage(picUrl: String?) {
        if(picUrl.isNullOrEmpty()) {
            return
        }
        Glide.with(requireContext().applicationContext)
            .load(picUrl)
            .error(PlayListItemPreview.ERROR_IMG)
            .placeholder(PlayListItemPreview.PLACEHOLDER_IMG)
            .into(mSongAlbumIv)
    }
}