package com.ltan.music.discovery.fragments

import android.content.Intent
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ltan.music.basemvp.MusicBaseFragment
import com.ltan.music.discovery.R
import kotlinx.android.synthetic.main.discovery_fragment.*

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
        const val TAG = "Disc/Frag"
        const val DRAWER_ACTION = "action_test_drawer"

        fun newInstance(): DiscoveryFragment {
            return DiscoveryFragment()
        }
    }

    override fun initLayout(): Int {
        return R.layout.discovery_fragment
    }

    override fun init(view: View) {
        drawerTest.setOnClickListener {
            LocalBroadcastManager.getInstance(view.context).sendBroadcast(Intent(DRAWER_ACTION))
        }
    }
}
