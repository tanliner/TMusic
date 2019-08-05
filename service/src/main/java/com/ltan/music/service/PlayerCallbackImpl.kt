package com.ltan.music.service

import com.ltan.music.common.LyricsObj
import com.ltan.music.common.MusicLog
import com.ltan.music.service.widget.PlayerPageController

/**
 * TMusic.com.ltan.music.service
 * call from MusicService
 *
 * @ClassName: PlayerCallbackImpl
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-28
 * @Version: 1.0
 */
class PlayerCallbackImpl(control: PlayerPageController) : MusicService.IPlayerCallback {
    companion object {
        const val TAG = "PlayerCallbackImpl"
    }

    private val controller = control
    private lateinit var lyricHighlight: LyricHighLight
    private var mViewPagerUpdater: IViewPagerUpdate? = null

    fun setLyricHighLight(highLight: LyricHighLight) {
        this.lyricHighlight = highLight
    }

    fun setViewPagerUpdate(pagerUpdater: IViewPagerUpdate) {
        mViewPagerUpdater = pagerUpdater
    }

    override fun onStart() {
        controller.setState(true)
        lyricHighlight.onStart()
    }

    override fun onPause() {
        MusicLog.w(TAG, "service PlayerCallbackImpl on pause")
        controller.setState(false)
        lyricHighlight.onPause()
    }

    override fun onNext(index: Int, curSong: SongPlaying) {
        mViewPagerUpdater?.onNext(index, curSong)
    }

    override fun onLast(index: Int, curSong: SongPlaying) {
        mViewPagerUpdater?.onLast(index, curSong)
    }

    override fun onCompleted(song: SongPlaying) {
        controller.setState(false)
    }

    override fun onBufferUpdated(per: Int) {
        MusicLog.v(TAG, "music source buffered $per")
    }

    override fun onLyricComplete(lyric: LyricsObj?) {
        lyricHighlight.onLyric(lyric)
    }

    /**
     * call on sub thread
     */
    override fun updateLyric(title: String?, txt: String?, index: Int) {
        if (txt.isNullOrEmpty()) {
            return
        }
        lyricHighlight.onHighLight(txt, index)
    }

    override fun onSongPicUpdated(url: String?) {
        lyricHighlight.onSongPreviewUpdate(url)
    }

    /**
     * ignore the [subtitle], artist1/artist2-xx
     */
    override fun updateTitle(title: String?, subtitle: String?, artist: String?) {
        // prevent compile error
        lyricHighlight.onSongChange(title.toString(), artist.toString())
    }
}