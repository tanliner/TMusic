package com.ltan.music.business.api

import com.ltan.music.basemvp.IBaseContract
import com.ltan.music.basemvp.RxUtils
import com.ltan.music.common.MusicLog
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

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

    /**
     * multi call on sub thread, will not switch to UI
     */
    protected fun <T> observeOnIO(observable: Flowable<T>): Flowable<T> {
        return observable
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
    }

    protected fun <T> observe(observable: Flowable<T>): Flowable<T> {
        return observable
            .compose(RxUtils.rxFlowableHelper())
    }
}