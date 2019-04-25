package com.ltan.music.index

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import butterknife.Unbinder
import com.ltan.music.index.view.PageIndicator
import kotterknife.bindView
import kotlin.reflect.KProperty

/**
 * TMusic.com.ltan.music.index
 *
 * @ClassName: IndexActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019/4/6
 * @Version: 1.0
 */
class IndexActivity : AppCompatActivity() {

    companion object {
        val TAG = "ltan/Index"
    }

    var mPageIndicator: PageIndicator by bindView(R.id.index_page_indicator)

    // @BindView(R2.id.index_vp)
    // val mViewPager: ViewPager
    var mViewPager: ViewPager by bindView(R.id.index_vp)

    private var unBinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        unBinder = ButterKnife.bind(this)

        val adapter = IndexPagerAdapter(this.supportFragmentManager)
        mPageIndicator.setViewPager(mViewPager)
        mViewPager.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        unBinder?.unbind()
    }
}

// so weird....
private operator fun Any.setValue(activity: Activity, property: KProperty<*>, v: View) {

}
