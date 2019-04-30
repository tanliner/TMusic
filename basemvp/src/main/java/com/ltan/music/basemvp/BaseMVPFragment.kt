package com.ltan.music.basemvp

import android.os.Bundle
import android.view.View

/**
 * TMusic.com.ltan.music.basemvp
 *
 * the base fragment like the declaration
 * abstract class BaseMVPFragment<V: IBaseView<V, P>, P : IBasePresenter<P, V>> : Fragment(), IBaseView<V, P> {}
 *
 * @ClassName: BaseMVPFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-28
 * @Version: 1.0
 */
abstract class BaseMVPFragment<P : IBaseContract.Presenter> : MusicBaseFragment(), IBaseContract.View {

    protected var mPresenter: P? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter = PresenterUtil.getBasePresenter(this.javaClass)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }
}
