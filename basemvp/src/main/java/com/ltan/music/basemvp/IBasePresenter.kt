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
interface IBasePresenter<P: IBasePresenter<P, V>, V: IBaseView<V, P>> {
    fun attachView(view: V)
    fun detachView()
}