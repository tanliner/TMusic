package com.ltan.music

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.ltan.music.adapter.MusicPagerAdapter
import com.ltan.music.basemvp.MusicBaseActivity
import com.ltan.music.common.StatusBarUtil
import com.ltan.music.view.PageIndicator
import com.ltan.music.widget.MenuItem
import kotterknife.bindView
import kotlin.reflect.KProperty

class MainActivity : MusicBaseActivity() {

    // I's works fine in Java
    // @BindView(R2.id.index_vp)
    // val mViewPager: ViewPager
    private val mViewPager: ViewPager by bindView(R.id.music_view_pager)
    private val mPageIndicator: PageIndicator by bindView(R.id.music_indicator)

    override fun initLayout(): Int {
        return R.layout.app_activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setColor(this, resources.getColor(R.color.colorPrimary))

        val adapter = MusicPagerAdapter(this.supportFragmentManager)
        mPageIndicator.setViewPager(mViewPager)
        mPageIndicator.addIndicators(getIndicators())
        mViewPager.adapter = adapter
    }

    private fun getIndicators(): ArrayList<MenuItem> {
        val itemMine = MenuItem(this)
        val itemDisc = MenuItem(this)
        val itemFriend = MenuItem(this)
        val itemVideo = MenuItem(this)
        val items = ArrayList<MenuItem>()
        itemMine.setTitle(getString(R.string.app_vp_indicator_mine))
        itemDisc.setTitle(getString(R.string.app_vp_indicator_discoveries))
        itemFriend.setTitle(getString(R.string.app_vp_indicator_friends))
        itemVideo.setTitle(getString(R.string.app_vp_indicator_videos))
        items.add(itemMine)
        items.add(itemDisc)
        items.add(itemFriend)
        items.add(itemVideo)
        return items
    }
}

// for butterknife in Kotlin, cannot initialize the view!!
private operator fun Any.setValue(activity: Activity, property: KProperty<*>, v: View) {

}