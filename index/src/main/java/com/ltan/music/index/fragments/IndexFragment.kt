package com.ltan.music.index.fragments

import android.os.Bundle
import android.view.View
import com.ltan.music.basemvp.BaseMVPFragment
import com.ltan.music.basemvp.setValue
import com.ltan.music.common.MusicLog
import com.ltan.music.index.R
import com.ltan.music.index.contract.IMineContract
import com.ltan.music.index.presenter.MinePresenter
import kotterknife.bindView

/**
 * TMusic.com.ltan.music.index.fragments
 *
 * @ClassName: IndexFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-26
 * @Version: 1.0
 */
class IndexFragment : BaseMVPFragment<MinePresenter>(), IMineContract.View {
    companion object {
        const val TAG = "ltan/Index-"
        fun newInstance(): IndexFragment {
            return IndexFragment()
        }
    }

    var mHeader: View by bindView(R.id.page_header)

    override fun initLayout(): Int {
        return R.layout.page_index
    }

    override fun testView(p: IMineContract.Presenter) {
        MusicLog.d(TAG, "testView called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // just a several log, TODO remove it
        mPresenter?.attachView(this)
        mPresenter?.start()
        mPresenter?.queryData()
        init(view)
    }

    fun init(view: View?) {
        mHeader.setOnClickListener { v -> justTest(v); justTest(mHeader) }
    }

    fun justTest(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}
