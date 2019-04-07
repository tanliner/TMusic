package com.ltan.music.index

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import butterknife.Unbinder
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

    var mTitle: TextView by bindView(R.id.index_title)

    // @BindView(R2.id.index_vp)
    // val mViewPager: ViewPager
    var mViewPager: ViewPager by bindView(R.id.index_vp)

    private var unBinder : Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        unBinder = ButterKnife.bind(this)

        val inflater = LayoutInflater.from(this)
        val page1 = inflater.inflate(R.layout.page_1, null, false)
        val page2 = inflater.inflate(R.layout.page_2, null, false)
        val page3 = inflater.inflate(R.layout.page_3, null, false)
        val viewList = ArrayList<View>()
        viewList.add(page1)
        viewList.add(page2)
        viewList.add(page3)
        for (i in 0..3) {
            Log.d(TAG, "this is a for loop $i")
        }
        val adapter = ViewPagerAdapter(this, viewList)
        // adapter.setViewList(viewList)
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
