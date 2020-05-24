package com.ltan.music.mine.contract

import com.ltan.music.basemvp.IBasePresenter
import com.ltan.music.basemvp.IBaseView
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrl
import com.ltan.music.mine.beans.PlayListDetailRsp

/**
 * TMusic.com.ltan.music.mine.contract
 *
 * @ClassName: SongListContract
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
interface SongListContract {
    interface View : IBaseView {
        fun onPlayListDetail(data: PlayListDetailRsp?)
        fun onSongUrl(songs: List<SongUrl>?)
        fun onSongDetail(songDetails: SongDetailRsp?)
    }
    interface Presenter : IBasePresenter<View> {

        /**
         * to get the current song list, favorite-song collection list
         * call back is [View.onPlayListDetail]
         */
        fun getPlayListDetail(id: Long)

        /**
         * [View.onSongUrl] some thing like: https://xx.xx.xx/xxx.mp3
         */
        fun getSongUrl(ids: String)
        /**
         * [View.onSongDetail] usually to get the song album, xxx.jpg
         */
        fun getSongDetail(ids: String, collector: String)
    }
}