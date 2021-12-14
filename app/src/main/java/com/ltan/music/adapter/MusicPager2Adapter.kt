package com.ltan.music.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ltan.music.FragmentFactory

class MusicPager2Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var fragClass: Array<Class<out Fragment>> = arrayOf()

    override fun getItemCount(): Int {
        return fragClass.size
    }

    override fun createFragment(position: Int): Fragment {
        return FragmentFactory.getInstance(fragClass[position])
    }
}
