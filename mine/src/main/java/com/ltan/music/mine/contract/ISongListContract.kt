package com.ltan.music.mine.contract

import com.ltan.music.basemvp.IBaseContract
import com.ltan.music.mine.beans.PlayListDetailRsp
import com.ltan.music.mine.beans.SongDetailRsp
import com.ltan.music.mine.beans.SongUrl

/**
 * TMusic.com.ltan.music.mine.contract
 *
 * @ClassName: ISongListContract
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
interface ISongListContract {
    interface View : IBaseContract.View<Presenter> {
        fun onPlayListDetail(data: PlayListDetailRsp?)
        fun onSongUrl(songs: List<SongUrl>?)
        fun onSongDetail(songDetails: SongDetailRsp?)
    }
    interface Presenter : IBaseContract.Presenter<View> {
        fun getPlayListDetail(id: Long)
        fun getSongUrl(ids: String)
        fun getSongDetail(ids: String, collector: String)
    }
}