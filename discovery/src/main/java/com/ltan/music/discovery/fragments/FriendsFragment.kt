package com.ltan.music.discovery.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.ltan.music.business.MusicBaseFragment
import com.ltan.music.business.setValue
import com.ltan.music.discovery.R
import kotterknife.bindView

/**
 * TMusic.com.ltan.music.discovery.fragments
 *
 * @ClassName: DiscoveryFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-27
 * @Version: 1.0
 */
class DiscoveryFragment : MusicBaseFragment() {

    companion object {
        const val TAG = "ltan/Friends-"
        fun newInstance(): DiscoveryFragment {
            return DiscoveryFragment()
        }
    }

    var mHeader: View by bindView(R.id.page_header)

    override fun initLayout(): Int {
        return R.layout.page_discovery
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    fun init(view: View?) {
        mHeader = view!!.findViewById(R.id.page_header)
        mHeader.setOnClickListener { v -> justTest(v); justTest(mHeader) }
    }

    fun justTest(v: View) {
        Log.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}

