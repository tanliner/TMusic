package com.ltan.music.basemvp

/**
 * TMusic.com.ltan.music.basemvp
 *
 * @ClassName: IBaseView
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-28
 * @Version: 1.0
 */
interface IBaseView<V: IBaseView<V, P>, P: IBasePresenter<P, V>> {
}