package com.ltan.music

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.jaeger.library.StatusBarUtil
import com.ltan.music.adapter.MusicPagerAdapter
import com.ltan.music.basemvp.MusicBaseActivity
import com.ltan.music.view.PageIndicator
import com.ltan.music.widget.MenuItem
import kotterknife.bindView

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
        val itemRadio = MenuItem(this)
        val itemMine = MenuItem(this)
        val itemDisc = MenuItem(this)
        val itemFollow = MenuItem(this)
        val itemCloud = MenuItem(this)
        val items = ArrayList<MenuItem>()

        itemDisc.setTitle(getString(R.string.app_vp_indicator_discoveries))
        itemRadio.setTitle(getString(R.string.app_vp_indicator_live))
        itemMine.setTitle(getString(R.string.app_vp_indicator_mine))

        itemFollow.setTitle(getString(R.string.app_vp_indicator_follow))
        itemCloud.setTitle(getString(R.string.app_vp_indicator_cloud))
        items.add(itemDisc)
        items.add(itemRadio)
        items.add(itemMine)
        items.add(itemFollow)
        items.add(itemCloud)
        return items
    }
}