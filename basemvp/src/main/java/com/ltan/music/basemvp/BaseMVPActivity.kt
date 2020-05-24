package com.ltan.music.basemvp

import android.os.Bundle
import com.ltan.music.common.MusicLog

/**
 * TMusic.com.ltan.music.basemvp
 *
 * @ClassName: BaseMVPActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
abstract class BaseMVPActivity<P : IBasePresenter<*>> : BaseActivity() {

    protected lateinit var mPresenter: P

    /**
     * init the presenter, such as attach the view
     */
    abstract fun initPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // mPresenter = PresenterUtil.getBasePresenter(this.javaClass)
        val p: P? = PresenterUtil.getBasePresenter(this.javaClass)
        p?.let { mPresenter = it }
        if(p == null) {
            MusicLog.e("The MVP activity must have a presenter")
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
