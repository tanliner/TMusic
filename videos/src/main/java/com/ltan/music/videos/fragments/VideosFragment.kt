package com.ltan.music.videos.fragments

import android.view.View
import android.widget.FrameLayout
import butterknife.BindView
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.common.MusicLog
import com.ltan.music.videos.R
import com.ltan.music.videos.R2

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

    @BindView(R2.id.page_header)
    lateinit var mHeader: FrameLayout

    override fun initLayout(): Int {
        return R.layout.videos_fragment
    }

    override fun init(view: View) {
        mHeader = view.findViewById(R.id.page_header)
        mHeader.setOnClickListener { v -> justTest(v); justTest(mHeader) }
    }

    fun justTest(v: View) {
        MusicLog.d("\nthis is a test message from$this, and mHeader is:$v")
    }
}

