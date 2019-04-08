package com.ltan.music.index

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * TMusic.com.ltan.music.index
 *
 * @ClassName: ViewPagerAdapter
 * @Description:
 * @Author: tanlin
 * @Date:   2019/4/7
 * @Version: 1.0
 */
class ViewPagerAdapter(context: Context, pages: ArrayList<View>) : PagerAdapter() {

    // private val inflater = LayoutInflater.from(context)
    // var mPages = ArrayList<View>()
    private val mPages = pages

    /**
     * @author tanlin
     * @desc  for update page content
     *
     * the new pagers in the [list]
     **/
    fun setViewList(list: ArrayList<View>) {
        mPages.clear()
        mPages.addAll(list)
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val page = mPages[position]
        container.addView(page)
        return page
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)
        val page = mPages[position]
        container.removeView(page)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return mPages.size
    }
}