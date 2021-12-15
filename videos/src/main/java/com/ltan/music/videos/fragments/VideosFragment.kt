package com.ltan.music.videos.fragments

import android.view.View
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.common.MusicLog
import com.ltan.music.videos.R
import kotlinx.android.synthetic.main.videos_fragment.*

/**
 * TMusic.com.ltan.music.videos.fragments
 *
 * @ClassName: VideosFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-27
 * @Version: 1.0
 */
class VideosFragment : MusicBaseFragment() {

    companion object {
        const val TAG = "Videos/Frag"
        fun newInstance(): VideosFragment {
            return VideosFragment()
        }
    }

    override fun initLayout(): Int {
        return R.layout.videos_fragment
    }

    override fun init(view: View) {
        page_header.setOnClickListener { v -> justTest(v); justTest(page_header) }
    }

    fun justTest(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}

