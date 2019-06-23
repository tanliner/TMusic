package com.ltan.music.service.presenter

import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.business.api.RxPresenter
import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrlRsp
import com.ltan.music.business.common.CommonApi
import com.ltan.music.service.contract.ServiceContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * TMusic.com.ltan.music.service.presenter
 *
 * @ClassName: ServicePresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-23
 * @Version: 1.0
 */
class ServicePresenter : RxPresenter<ServiceContract.View>(), ServiceContract.Presenter {
    override fun getSongDetail(ids: String, collector: String) {
        ApiProxy.instance.getApi(CommonApi::class.java)
            .getSongDetail(ids, collector)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe(object : NormalSubscriber<SongDetailRsp>() {
                override fun onNext(t: SongDetailRsp?) {
                    mView.onSongDetail(t)
                }
            })
    }

    override fun getSongUrl(ids: String) {
        ApiProxy.instance.getApi(CommonApi::class.java)
            .getSongUrl(ids)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe(object : NormalSubscriber<SongUrlRsp>() {
                override fun onNext(t: SongUrlRsp?) {
                    mView.onSongUrl(t?.data)
                }
            })
    }
}