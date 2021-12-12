package com.ltan.music.friends.fragments

import android.view.View
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.basemvp.setValue
import com.ltan.music.common.MusicLog
import com.ltan.music.friends.R
import kotterknife.bindView

/**
 * TMusic.com.ltan.music.friends.fragments
 *
 * @ClassName: FollowersFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2021-12-12
 * @Version: 1.0
 */
class FollowersFragment : MusicBaseFragment() {

    companion object {
        const val TAG = "Friends/Frag"
        fun newInstance(): FollowersFragment {
            return FollowersFragment()
        }
    }

    var mHeader: View by bindView(R.id.page_header)

    override fun initLayout(): Int {
        return R.layout.followers_fragment
    }

    override fun init(view: View) {
        mHeader = view.findViewById(R.id.page_header)
        mHeader.setOnClickListener { v -> justTest(v); justTest(mHeader) }
    }

    fun justTest(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}

