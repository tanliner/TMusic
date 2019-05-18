package com.ltan.music.business.api

import com.ltan.music.basemvp.IBaseContract
import com.ltan.music.common.MusicLog

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: RxPresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-30
 * @Version: 1.0
 */
open class RxPresenter<V: IBaseContract.View<*>> : IBaseContract.Presenter<V> {
// open class RxPresenter<V: IBaseContract.View<*>> : BaseMVPPresenter<V>() {

    companion object {
        const val TAG = "RxPresenter/"
    }

    protected lateinit var mView: V

    /**
     * When async task published, should check the view's state
     */
    protected var mViewAttached: Boolean = false

    override fun attachView(view: V) {
        MusicLog.d(TAG, "attachView called $view")
        mView = view
        mViewAttached = true
    }

    override fun start() {
    }

    override fun detachView() {
        mViewAttached = false
    }
}