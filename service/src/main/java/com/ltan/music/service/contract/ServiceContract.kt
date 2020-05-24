package com.ltan.music.service.contract

import com.ltan.music.basemvp.IBasePresenter
import com.ltan.music.basemvp.IBaseView
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrl

/**
 * TMusic.com.ltan.music.service.contract
 * Workflow like this: getSongDetail -> onSongDetail -> getSongUrl -> onSongUrl
 *
 * @ClassName: ServiceContract
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-23
 * @Version: 1.0
 */
interface ServiceContract {

    interface View : IBaseView {
        fun onSongUrl(songs: List<SongUrl>?)
        fun onSongDetail(songDetails: SongDetailRsp?)
    }

    interface Presenter : IBasePresenter<View> {
        fun getSongDetail(ids: String, collector: String)
        fun getSongUrl(ids: String)
    }
}