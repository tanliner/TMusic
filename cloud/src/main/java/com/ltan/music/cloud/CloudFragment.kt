package com.ltan.music.cloud

import android.view.View
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.common.MusicLog
import kotlinx.android.synthetic.main.cloud_fragment.*

/**
 * TMusic.com.ltan.music.cloud
 *
 * @ClassName: VideosFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2021-12-12
 * @Version: 1.0
 */
class CloudFragment : MusicBaseFragment() {

    companion object {
        const val TAG = "Cloud/Frag"
        fun newInstance(): CloudFragment {
            return CloudFragment()
        }
    }

    override fun initLayout(): Int {
        return R.layout.cloud_fragment
    }

    override fun init(view: View) {
        page_header.setOnClickListener { v -> justTest(v); justTest(page_header) }
    }

    fun justTest(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}
