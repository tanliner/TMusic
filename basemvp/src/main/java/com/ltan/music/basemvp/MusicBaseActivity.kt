package com.ltan.music.basemvp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ltan.music.common.MusicLog

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

    protected val TAG: String = this::class.java.simpleName

    abstract fun initLayout(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = initLayout()
        if (layoutId <= 0) {
            MusicLog.e(TAG, "must have a validate the layout")
            return
        }
        setContentView(layoutId)
    }
}
