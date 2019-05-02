package com.ltan.music.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ltan.music.FragmentFactory
import com.ltan.music.R
import com.ltan.music.discovery.fragments.DiscoveryFragment
import com.ltan.music.friends.fragments.FriendsFragment
import com.ltan.music.mine.fragments.MineFragment
import com.ltan.music.videos.fragments.VideosFragment

/**
 * TMusic.com.ltan.music.adapter
 *
 * because those page has plenty data, maybe lead a memory problem, so use the FragmentStatePagerAdapter,
 * it can save the fragments state
 *
 * @ClassName: MusicPagerAdapter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-27
 * @Version: 1.0
 */
class MusicPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    init {
    }

    private val pages: IntArray =
        intArrayOf(R.layout.mine_fragment, R.layout.discovery_fragment, R.layout.friends_fragment, R.layout.videos_fragment)
    // todo factory pattern generate the fragment instance
    private val frag = arrayOf(MineFragment.newInstance(), DiscoveryFragment.newInstance(), FriendsFragment.newInstance(), VideosFragment.newInstance())
    private val fragClass = arrayOf(MineFragment::class.java, DiscoveryFragment::class.java, FriendsFragment::class.java, VideosFragment::class.java)

    override fun getItem(position: Int): Fragment? {
        // return MusicBaseFragment.newInstance(pages[position])
        // return frag[position]
        return FragmentFactory.getInstance(fragClass[position])
    }

    override fun getCount(): Int {
        return pages.size
    }
}