package com.ltan.music.mine

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.ltan.music.common.LyricsObj
import com.ltan.music.common.MusicLog
import com.ltan.music.service.MusicService
import com.ltan.music.service.SongPlaying
import com.ltan.music.widget.MusicPlayerController

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: PlayerCallbackImpl
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-21
 * @Version: 1.0
 */
class PlayerCallbackImpl(control: MusicPlayerController) : MusicService.IPlayerCallback {
    companion object {
        const val TAG = "PlayerCallbackImpl"
    }

    private val controller = control
    override fun onStart() {
        controller.setState(true)
    }

    override fun onPause() {
        controller.setState(false)
    }

    override fun onNext(index: Int, curSong: SongPlaying) {
    }

    override fun onLast(index: Int, curSong: SongPlaying) {
    }

    override fun onCompleted(song: SongPlaying) {
        controller.setState(false)
        controller.updateDisplay(song.title, song.subtitle)
    }

    override fun onBufferUpdated(per: Int) {
        MusicLog.v("music source buffered $per")
    }

    override fun onLyricComplete(lyric: LyricsObj?) {
    }

    /**
     * call on sub thread
     */
    override fun updateLyric(curSong: SongPlaying, txt: String?, index: Int) {
        if (txt.isNullOrEmpty()) {
            // return
        }
        if (curSong.title != controller.getTitle()) {
            controller.post { controller.updateTitle(curSong.title) }
        }
        var summary = txt
        if (TextUtils.isEmpty(txt)) {
            summary = if (index > 1) {
                curSong.lyrics?.songTexts?.get(index - 1)?.txt
            } else {
                curSong.subtitle
            }
        }
        controller.post { controller.updateSummary(summary) }
    }

    /**
     * should run on UI thread
     */
    override fun onSongPicUpdated(url: String?) {
        Glide.with(controller.context)
            .load(url)
            .into(controller.mPreviewIv)
    }

    override fun updateTitle(title: String?, subtitle: String?, artist: String?) {
        controller.updateDisplay(title, subtitle)
    }
}