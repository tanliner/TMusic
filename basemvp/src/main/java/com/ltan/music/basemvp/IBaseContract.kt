package com.ltan.music.basemvp

/**
 * TMusic.com.ltan.music.basemvp
 *
 * @ClassName: IBaseContract
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-30
 * @Version: 1.0
 */
interface IBaseContract {
    interface View<P> : IBaseView<P>

    interface Presenter<V: View<*>> : IBasePresenter<V>
}