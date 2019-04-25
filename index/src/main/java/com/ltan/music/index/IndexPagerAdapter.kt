package com.ltan.music.index

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ltan.music.index.fragments.FragmentTemplate

/**
 * TMusic.com.ltan.music.index
 *
 * because those page has plenty data, maybe lead a memory problem, so use the FragmentStatePagerAdapter,
 * it can save the fragments state
 *
 * @ClassName: IndexPagerAdapter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-26
 * @Version: 1.0
 */
class IndexPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    init {
    }

    var pages: IntArray = intArrayOf(R.layout.page_mine, R.layout.page_discovery, R.layout.page_friends, R.layout.page_videos)

    override fun getItem(position: Int): Fragment {
        return FragmentTemplate.newInstance(pages[position])
    }

    override fun getCount(): Int {
        return pages.size
    }
}