package com.ltan.music.basemvp

/**
 * TMusic.com.ltan.music.basemvp
 *
 * @ClassName: IBasePresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-28
 * @Version: 1.0
 */
interface IBasePresenter<V> {
    /**
     * T? means, maybe no T, will be called after viewCreated
     * */
    fun attachView(view: V)
    /**
     * will be called after resume
     * */
    fun start()
    /**
     * will be called after destroyView
     * */
    fun detachView()
}