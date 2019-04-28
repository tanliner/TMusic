package com.ltan.music.basemvp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

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
abstract class BaseMVPFragment<P : BasePresenter> : Fragment(), BaseView {
    // It's should be inherited
    protected var mPresenter: P? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mPresenter = PresenterUtil.getBasePresenter(this.javaClass)

        // Note: smart case to 'P' impossible, because mPresenter is mutable property
        // that could have been changed by this time.
        // if (mPresenter != null) {
        //     mPresenter.attachView(this)
        // }
        mPresenter?.attachView(this)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }
}

interface BaseView : IBaseView<BaseView, BasePresenter> {}

interface BasePresenter : IBasePresenter<BasePresenter, BaseView> {}
