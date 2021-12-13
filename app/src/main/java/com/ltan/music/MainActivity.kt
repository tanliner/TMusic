package com.ltan.music

import android.os.Bundle
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jaeger.library.StatusBarUtil
import com.ltan.music.adapter.MusicPager2Adapter
import com.ltan.music.basemvp.MusicBaseActivity
import com.ltan.music.cloud.CloudFragment
import com.ltan.music.discovery.fragments.DiscoveryFragment
import com.ltan.music.friends.fragments.FollowersFragment
import com.ltan.music.live.LiveFragment
import com.ltan.music.mine.fragments.MineFragment
import com.ltan.music.widget.MenuItem
import kotlinx.android.synthetic.main.app_activity_main.*

class MainActivity : MusicBaseActivity() {

    private var lastPagerPosition = 0
    override fun initLayout(): Int {
        return R.layout.app_activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setColor(this, resources.getColor(R.color.colorPrimary))
        initPager()
        setupTabCustomView()
    }

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
                tabLayout.getTabAt(lastPagerPosition)?.customView?.findViewById<TextView>(R.id.tv_menu_item_title)?.setTextColor(resources.getColor(android.R.color.black))
                tabLayout.getTabAt(position)?.customView?.findViewById<TextView>(R.id.tv_menu_item_title)?.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                lastPagerPosition = position
            }
        })
    }

    private fun setupTabCustomView() {
        TabLayoutMediator(tabLayout, music_view_pager2) { _, _ -> }.attach()
        val itemRadio = MenuItem(this)
        val itemMine = MenuItem(this)
        val itemDisc = MenuItem(this)
        val itemFollow = MenuItem(this)
        val itemCloud = MenuItem(this)
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
