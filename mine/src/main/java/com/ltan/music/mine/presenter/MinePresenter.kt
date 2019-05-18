package com.ltan.music.mine.presenter

import com.ltan.music.business.api.RxPresenter
import com.ltan.music.common.MusicLog
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

    override fun start() {
        super.start()
        MusicLog.d(TAG, "start $mView")
    }

    override fun queryData() {
        MusicLog.d(TAG, "queryData")
    }

     override fun attachView(view: IMineContract.View) {
        super.attachView(view)
        MusicLog.d(TAG, "attachView $view vs $mView")
    }

    override fun detachView() {
        super.detachView()
        MusicLog.d(TAG, "detachView $mView")
    }
}