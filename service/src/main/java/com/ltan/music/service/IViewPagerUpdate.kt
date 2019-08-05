package com.ltan.music.service

/**
 * TMusic.com.ltan.music.service
 * for view-pager update, horizontal scroll
 *
 * @ClassName: IViewPagerUpdate
 * @Description:
 * @Author: tanlin
 * @Date:   2019-08-05
 * @Version: 1.0
 */
interface IViewPagerUpdate {
    /**
     * play next song, trigger by click 'next' button
     * [index] will switch to index
     * [curSong] the next song
     */
    fun onNext(index: Int, curSong: SongPlaying)

    /**
     * play last song, trigger by click 'last' button
     * [index] will switch to index
     * [curSong] the last song
     */
    fun onLast(index: Int, curSong: SongPlaying)
}