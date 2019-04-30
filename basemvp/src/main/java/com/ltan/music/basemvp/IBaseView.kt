package com.ltan.music.basemvp

/**
 * TMusic.com.ltan.music.basemvp
 *
 * `*` is the MVP key, just like code below:
 * {
 * @code IBaseView<IBasePresenter<*>>
 * @code IBasePresenter<T : IBaseView>
 * }
 *
 *
 * @ClassName: IBaseView
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-28
 * @Version: 1.0
 */
interface IBaseView<P : IBasePresenter<*>> {
}