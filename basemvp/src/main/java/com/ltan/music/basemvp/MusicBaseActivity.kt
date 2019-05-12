package com.ltan.music.basemvp

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.ltan.music.common.MusicLog
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
abstract class MusicBaseActivity : AppCompatActivity() {
    companion object {
        var TAG: String = this::class.java.simpleName
    }
    private lateinit var unBinder: Unbinder

    abstract fun initLayout(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = initLayout()
        if (layoutId <= 0) {
            MusicLog.e(TAG, "must have a validate the layout")
            return
        }
        setContentView(initLayout())
        unBinder = ButterKnife.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unBinder.unbind()
    }
}

// for butterknife in Kotlin
private operator fun Any.setValue(activity: Activity, property: KProperty<*>, v: View) {

}