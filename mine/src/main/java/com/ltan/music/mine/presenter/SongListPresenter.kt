package com.ltan.music.mine.presenter

import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.business.api.RxPresenter
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrlRsp
import com.ltan.music.business.common.CommonApi
import com.ltan.music.mine.MineApi
import com.ltan.music.mine.beans.PlayListDetailRsp
import com.ltan.music.mine.contract.SongListContract

/**
 * TMusic.com.ltan.music.mine.presenter
 *
 * @ClassName: SongListPresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
class SongListPresenter : RxPresenter<SongListContract.View>(), SongListContract.Presenter {

    override fun getPlayListDetail(id: Long) {
        observe(
            ApiProxy.instance.getApi(MineApi::class.java)
                .getPlayLisDetail(id))
            .safeSubscribe(object : NormalSubscriber<PlayListDetailRsp>() {
                override fun onNext(t: PlayListDetailRsp?) {
                    mView.onPlayListDetail(t)
                }
            })
    }

    override fun getSongUrl(ids: String) {
        observe(ApiProxy.instance.getApi(CommonApi::class.java)
            .getSongUrl(ids))
            .safeSubscribe(object : NormalSubscriber<SongUrlRsp>(){
                override fun onNext(t: SongUrlRsp?) {
                    mView.onSongUrl(t?.data)
                }
            })
    }

    override fun getSongDetail(ids: String, collector: String) {
        observe(ApiProxy.instance.getApi(CommonApi::class.java)
            .getSongDetail(ids, collector))
            .safeSubscribe(object : NormalSubscriber<SongDetailRsp>(){
                override fun onNext(t: SongDetailRsp?) {
                    mView.onSongDetail(t)
                }
            })
    }
}