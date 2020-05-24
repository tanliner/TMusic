package com.ltan.music.discovery.fragments

import android.view.View
import android.widget.FrameLayout
import butterknife.BindView
import com.ltan.music.basemvp.BaseFragment
import com.ltan.music.common.MusicLog
import com.ltan.music.discovery.R
import com.ltan.music.discovery.R2


/**
 * TMusic.com.ltan.music.discovery.fragments
 *
 * @ClassName: DiscoveryFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-27
 * @Version: 1.0
 */
class DiscoveryFragment : BaseFragment() {

    companion object {
        const val TAG = "Disc/Frag"
        fun newInstance(): DiscoveryFragment {
            return DiscoveryFragment()
        }
    }

    @BindView(R2.id.page_header)
    lateinit var mHeader: FrameLayout

    override fun contentLayout(): Int {
        return R.layout.discovery_fragment
    }

    override fun init(view: View) {
        mHeader = view.findViewById(R.id.page_header)
        mHeader.setOnClickListener { v -> justTest(v); justTest(mHeader) }
    }

    fun justTest(v: View) {
        MusicLog.d("\nthis is a test message from$this, and mHeader is:$v")
    }
}

