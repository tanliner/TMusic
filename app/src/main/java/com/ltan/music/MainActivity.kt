package com.ltan.music

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import butterknife.Unbinder
import com.ltan.music.adapter.MusicPagerAdapter
import com.ltan.music.view.PageIndicator
import kotterknife.bindView
import kotlin.reflect.KProperty

class MainActivity : AppCompatActivity() {

    // I's works fine in Java
    // @BindView(R2.id.index_vp)
    // val mViewPager: ViewPager
    private val mViewPager: ViewPager by bindView(R.id.music_view_pager)
    private val mPageIndicator: PageIndicator by bindView(R.id.music_indicator)

    private var unBinder: Unbinder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_main)
        unBinder = ButterKnife.bind(this)

        val adapter = MusicPagerAdapter(this.supportFragmentManager)
        mPageIndicator.setViewPager(mViewPager)
        mViewPager.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        unBinder?.unbind()
    }
}

// for butterknife in Kotlin, cannot initialize the view!!
private operator fun Any.setValue(activity: Activity, property: KProperty<*>, v: View) {

}