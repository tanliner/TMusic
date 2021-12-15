package com.ltan.music.friends.fragments

import android.view.View
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.common.MusicLog
import com.ltan.music.friends.R
import kotlinx.android.synthetic.main.friends_fragment.*

/**
 * TMusic.com.ltan.music.friends.fragments
 *
 * @ClassName: FriendsFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-27
 * @Version: 1.0
 */
class FriendsFragment : MusicBaseFragment() {

    companion object {
        const val TAG = "Friends/Frag"
        fun newInstance(): FriendsFragment {
            return FriendsFragment()
        }
    }

    override fun initLayout(): Int {
        return R.layout.friends_fragment
    }

    override fun init(view: View) {
        page_header.setOnClickListener { v -> justTest(v); justTest(page_header) }
    }

    fun justTest(v: View) {
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}

