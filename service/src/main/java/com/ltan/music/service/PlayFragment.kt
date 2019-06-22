package com.ltan.music.service

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.common.EasyBlur
import com.ltan.music.common.StatusBar
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
class PlayFragment : MusicBaseFragment() {
    companion object {
        fun newInstance(): PlayFragment {
            return PlayFragment()
        }
    }

    private lateinit var mCurSong: SongPlaying
    private val mPreviewBg: ImageView by bindView(R.id.iv_play_service_bg)
    private val mNavIcon: ImageView by bindView(R.id.iv_play_service_back)
    private val mSongName: TextView by bindView(R.id.tv_play_service_song_name)
    private val mSongArtist: TextView by bindView(R.id.tv_play_service_song_artists)

    override fun initLayout(): Int {
        return R.layout.service_player
    }

    override fun init(view: View) {
        super.init(view)
        processArgs()

        val mlp = mNavIcon.layoutParams as RelativeLayout.LayoutParams
        mlp.topMargin = StatusBar.getStatusBarHeight(this.requireContext())
        mNavIcon.layoutParams = mlp

        mNavIcon.setOnClickListener { onBack() }
        mSongName.text = mCurSong.title
        mSongArtist.text = mCurSong.subtitle

        val appCtx = requireContext().applicationContext
        Glide.with(appCtx)
            .load(mCurSong.picUrl)
            .error(PlayListItemPreview.ERROR_IMG)
            .placeholder(PlayListItemPreview.PLACEHOLDER_IMG)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    mPreviewBg.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            mPreviewBg.isDrawingCacheEnabled = true
                            val bitmap = mPreviewBg.drawingCache
                            mPreviewBg.viewTreeObserver.removeOnPreDrawListener(this)
                            val result = EasyBlur.sInstance
                                .with(appCtx)
                                .bitmap(bitmap)
                                .radius(25)
                                .scale(30)
                                .blur()
                            mPreviewBg.setImageBitmap(result)
                            mPreviewBg.isDrawingCacheEnabled = false
                            return false
                        }
                    })
                    return false
                }
            })
            .transform(BlurTransformation(20, 25)) /* fast blur in Java layer */
            .into(mPreviewBg)
    }

    private fun processArgs() {
        val args = arguments ?: return
        mCurSong = args.get(PlayerActivity.ARG_OBJ) as SongPlaying
    }
}