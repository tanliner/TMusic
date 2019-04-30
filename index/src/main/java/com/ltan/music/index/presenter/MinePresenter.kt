package com.ltan.music.index.presenter

import com.ltan.music.basemvp.IBaseContract
import com.ltan.music.business.api.RxPresenter
import com.ltan.music.common.MusicLog
import com.ltan.music.index.contract.IMineContract

/**
 * TMusic.com.ltan.music.index.presenter
 *
 * @ClassName: MinePresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-30
 * @Version: 1.0
 */
open class MinePresenter : RxPresenter(), IMineContract.Presenter, IMineContract.View {

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

    override fun attachView(view: IBaseContract.View?) {
        super.attachView(view)
        MusicLog.d(TAG, "attachView $view vs $mView")
    }

    override fun detachView() {
        super.detachView()
        MusicLog.d(TAG, "detachView $mView")
    }

    override fun testView(p: IMineContract.Presenter) {
        MusicLog.d(TAG, "testView $p")
    }
}