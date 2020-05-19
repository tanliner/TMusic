package com.ltan.music

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import com.ltan.music.adapter.MusicPagerAdapter
import com.ltan.music.basemvp.BaseMVPActivity
import com.ltan.music.contract.LoginContract
import com.ltan.music.presenter.LoginPresenter
import com.ltan.music.view.PageIndicator
import com.ltan.music.widget.MenuItem

class MainActivity : BaseMVPActivity<LoginPresenter>(), LoginContract.View {

    // I's works fine in Java
    // @BindView(R2.id.index_vp)
    // val mViewPager: ViewPager
    @BindView(R2.id.music_view_pager)
    lateinit var mViewPager: ViewPager
    @BindView(R2.id.music_indicator)
    lateinit var mPageIndicator: PageIndicator
    @BindView(R2.id.dl_app_root)
    lateinit var mDrawerLayout: DrawerLayout
    @BindView(R2.id.iv_app_drawer_menu)
    lateinit var mMenuButton: ImageView
    @BindView(R2.id.tv_app_logout)
    lateinit var mLogoutTv: TextView
    @BindView(R2.id.rv_app_drawer_items)
    lateinit var mDrawerItems: RecyclerView

    override fun initLayout(): Int {
        return R.layout.app_activity_main
    }

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        val adapter = MusicPagerAdapter(this.supportFragmentManager)
        mPageIndicator.setViewPager(mViewPager)
        mPageIndicator.addIndicators(getIndicators())
        mViewPager.adapter = adapter

        mMenuButton.setOnClickListener {
            mDrawerLayout.openDrawer(Gravity.START)
        }

        mLogoutTv.setOnClickListener {
            mPresenter.logout()
        }

        mDrawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            // menu rotation
            val degree = 90
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                mMenuButton.rotation = getDegree(slideOffset)
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            fun getDegree(offset: Float): Float {
                return degree * offset
            }
        })
    }

    override fun onLogoutSuccess() {
        startActivity(Intent(baseContext, LoginActivity::class.java))
        finish()
    }

    override fun onLoginStatus(code: Int) {
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