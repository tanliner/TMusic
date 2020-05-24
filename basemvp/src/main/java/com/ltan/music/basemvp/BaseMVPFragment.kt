package com.ltan.music.basemvp

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
abstract class BaseMVPFragment<P : IBasePresenter<*>> : BaseFragment() {

    protected lateinit var mPresenter: P

    /**
     * init the presenter, such as attach the view
     */
    abstract fun attachView()

    override fun init(view: View) {
        val p: P? = PresenterUtil.getBasePresenter(this.javaClass)
        p?.let { mPresenter = it }
        if(p == null) {
            MusicLog.e("The MVP fragment must have a presenter")
            return
        }
        mPresenter = p
        attachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
