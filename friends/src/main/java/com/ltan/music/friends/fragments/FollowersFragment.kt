package com.ltan.music.friends.fragments

import android.view.View
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.common.MusicLog
import com.ltan.music.friends.R
import kotlinx.android.synthetic.main.followers_fragment.*

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

    override fun initLayout(): Int {
        return R.layout.followers_fragment
    }

    override fun init(view: View) {
        page_header.setOnClickListener { v -> justTest(v); justTest(page_header) }
    }

    fun justTest(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}

