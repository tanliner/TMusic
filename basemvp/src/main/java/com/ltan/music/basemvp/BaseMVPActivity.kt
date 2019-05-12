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
abstract class BaseMVPActivity<P : IBaseContract.Presenter> : MusicBaseActivity(), IBaseContract.View {

    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // mPresenter = PresenterUtil.getBasePresenter(this.javaClass)
        val p: P? = PresenterUtil.getBasePresenter(this.javaClass)
        p?.let { mPresenter = it }
        if(p == null) {
            MusicLog.e(TAG, "must have a presenter")
            return
        }
        mPresenter = p
        mPresenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
