package com.ltan.music.friends.fragments

import android.os.Bundle
import android.view.View
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.basemvp.setValue
import com.ltan.music.common.MusicLog
import com.ltan.music.friends.R
import kotterknife.bindView

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
        const val TAG = "ltan/Friends-"
        fun newInstance(): FriendsFragment {
            return FriendsFragment()
        }
    }

    var mHeader: View by bindView(R.id.page_header)

    override fun initLayout(): Int {
        return R.layout.page_friends
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
        MusicLog.d(TAG, "\nthis is a test message from$this, and mHeader is:$v")
    }
}

