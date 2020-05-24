package com.ltan.music.basemvp

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import butterknife.ButterKnife
import butterknife.Unbinder
import com.ltan.music.common.MusicLog
import me.yokeyword.fragmentation.SupportActivity
import kotlin.reflect.KProperty

/**
 * TMusic.com.ltan.music.basemvp
 *
 * @ClassName: MusicBaseActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
abstract class BaseActivity : SupportActivity() {

    private var unBinder: Unbinder = Unbinder.EMPTY

    abstract fun contentLayout(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindowFlag()
        val layoutId = contentLayout()
        if (layoutId <= 0) {
            MusicLog.e("must have a validate the layout")
            return
        }
        setContentView(layoutId)
        unBinder = ButterKnife.bind(this)
    }

    /**
     * for layout immersive
     */
    private fun initWindowFlag() {
        // min_sdk = 22 > 21
        // https://juejin.im/entry/59c62b8ef265da065476d827
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(android.R.color.transparent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unBinder.unbind()
    }
}

// for butterknife in Kotlin
private operator fun Any.setValue(activity: Activity, property: KProperty<*>, v: View) {

}