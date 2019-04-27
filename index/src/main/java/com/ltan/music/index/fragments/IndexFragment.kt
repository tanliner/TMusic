package com.ltan.music.index.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.ltan.music.business.MusicBaseFragment
import com.ltan.music.business.setValue
import com.ltan.music.index.R
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
class IndexFragment : MusicBaseFragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    fun init(view: View?) {
        mHeader.setOnClickListener { v -> justTest(v); justTest(mHeader) }
    }

    fun justTest(v: View) {
        Log.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}
