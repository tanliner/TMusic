package com.ltan.music

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.ltan.music.adapter.MusicPager2Adapter
import com.ltan.music.cloud.CloudFragment
import com.ltan.music.discovery.fragments.DiscoveryFragment
import com.ltan.music.friends.fragments.FollowersFragment
import com.ltan.music.live.LiveFragment
import com.ltan.music.mine.fragments.MineFragment
import com.ltan.music.widget.MenuItem
import kotlinx.android.synthetic.main.app_fragment_main.*

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPager()
        setupTabCustomView()
        LocalBroadcastManager.getInstance(view.context)
            .registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    drawerLayout.openDrawer(Gravity.LEFT)
                }
            }, IntentFilter(DiscoveryFragment.DRAWER_ACTION))

        drawerNavigation.setNavigationItemSelectedListener {
            Log.i("ltan/MainFragment", "setNavigationItemSelectedListener: ${it.title}")
            false
        }
    }

    private var lastPagerPosition = 0

    private fun initPager() {
        val adapter = MusicPager2Adapter(this)
        adapter.fragClass = arrayOf(
            DiscoveryFragment::class.java,
            LiveFragment::class.java,
            MineFragment::class.java,
            FollowersFragment::class.java,
            CloudFragment::class.java
        )
        music_view_pager2.adapter = adapter
        music_view_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(lastPagerPosition)?.customView?.findViewById<TextView>(R.id.tv_menu_item_title)
                    ?.setTextColor(resources.getColor(android.R.color.black))
                tabLayout.getTabAt(position)?.customView?.findViewById<TextView>(R.id.tv_menu_item_title)
                    ?.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                lastPagerPosition = position
            }
        })
    }

    private fun setupTabCustomView() {
        TabLayoutMediator(tabLayout, music_view_pager2) { _, _ -> }.attach()
        val context = tabLayout.context
        val itemRadio = MenuItem(context)
        val itemMine = MenuItem(context)
        val itemDisc = MenuItem(context)
        val itemFollow = MenuItem(context)
        val itemCloud = MenuItem(context)
        itemDisc.setTitle(getString(R.string.app_vp_indicator_discoveries))
        itemRadio.setTitle(getString(R.string.app_vp_indicator_live))
        itemMine.setTitle(getString(R.string.app_vp_indicator_mine))
        itemFollow.setTitle(getString(R.string.app_vp_indicator_follow))
        itemCloud.setTitle(getString(R.string.app_vp_indicator_cloud))

        tabLayout.getTabAt(0)?.customView = itemDisc
        tabLayout.getTabAt(1)?.customView = itemRadio
        tabLayout.getTabAt(2)?.customView = itemMine
        tabLayout.getTabAt(3)?.customView = itemFollow
        tabLayout.getTabAt(4)?.customView = itemCloud
    }
}
