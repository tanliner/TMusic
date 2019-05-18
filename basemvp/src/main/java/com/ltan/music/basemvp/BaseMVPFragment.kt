package com.ltan.music.basemvp

import android.os.Bundle
import android.view.View
import com.ltan.music.common.MusicLog

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
abstract class BaseMVPFragment<P : IBaseContract.Presenter<*>> : MusicBaseFragment() {

    protected lateinit var mPresenter: P

    /**
     * init the presenter, such as attach the view
     */
    abstract fun initPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // mPresenter = PresenterUtil.getBasePresenter(this.javaClass)
        val p: P? = PresenterUtil.getBasePresenter(this.javaClass)
        p?.let { mPresenter = it }
        if(p == null) {
            MusicLog.e(TAG, "The MVP fragment must have a presenter")
            return
        }
        mPresenter = p
        initPresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
