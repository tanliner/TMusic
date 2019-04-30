package com.ltan.music.business.api

import com.ltan.music.basemvp.BaseMVPPresenter
import com.ltan.music.basemvp.IBaseContract

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: RxPresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-30
 * @Version: 1.0
 */
open class RxPresenter : BaseMVPPresenter() {
    protected var mView: IBaseContract.View? = null

    override fun attachView(view: IBaseContract.View?) {
        mView = view
    }

    override fun start() {
    }

    override fun detachView() {
        mView = null
    }
}