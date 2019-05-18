package com.ltan.music.mine.presenter

import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.business.api.RxPresenter
import com.ltan.music.mine.MineApi
import com.ltan.music.mine.beans.PlayListRsp
import com.ltan.music.mine.beans.SongSubCunt
import com.ltan.music.mine.contract.IMineContract

/**
 * TMusic.com.ltan.music.index.presenter
 *
 * @ClassName: MinePresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-30
 * @Version: 1.0
 */
class MinePresenter : RxPresenter<IMineContract.View>(), IMineContract.Presenter {

    companion object {
        const val TAG: String = "ltan/MinePresenter-"
    }

    override fun subcount() {
        observe(ApiProxy.instance.getApi(MineApi::class.java)
            .subCount())
            .safeSubscribe(object : NormalSubscriber<SongSubCunt>() {
                override fun onNext(t: SongSubCunt?) {
                    mView.onSubcount(t)
                }
            })
    }

    override fun getPlayList(uid: Long) {
        observe(ApiProxy.instance.getApi(MineApi::class.java)
            .getPlayList(uid))
            .safeSubscribe(object : NormalSubscriber<PlayListRsp>() {
                override fun onNext(t: PlayListRsp?) {
                    if(t != null) {
                        mView.onPlayList(t.playlist)
                    }
                }
            })
    }
}