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
    private var mViewPagerUpdater: IViewPagerUpdate? = null

    override fun onStart() {
        controller.setState(true)
    }

    override fun onPause() {
        MusicLog.w(TAG, "service PlayerCallbackImpl on pause")
        controller.setState(false)
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
    }

    /**
     * call on sub thread
     */
    override fun updateLyric(curSong: SongPlaying, txt: String?, index: Int) {
        if (txt.isNullOrEmpty()) {
            // return
        }
    }

    override fun onSongPicUpdated(url: String?) {
    }

    /**
     * ignore the [subtitle], artist1/artist2-xx
     */
    override fun updateTitle(title: String?, subtitle: String?, artist: String?) {
        // prevent compile error
    }
}