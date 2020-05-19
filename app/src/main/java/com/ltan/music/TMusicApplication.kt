package com.ltan.music

import android.app.Application
import android.content.Context
import com.ltan.music.common.BaseApplication
import com.tencent.mmkv.MMKV
import me.jessyan.autosize.AutoSizeConfig

/**
 * TMusic.com.ltan.music
 *
 * @ClassName: TMusicApplication
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class TMusicApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // todo multi process check
        BaseApplication.initContext(this)
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        if (BuildConfig.AUTO_SIZE) {
            configAutoSize()
        }
    }

    private fun configAutoSize() {
        AutoSizeConfig.getInstance().setLog(BuildConfig.DEBUG)
        AutoSizeConfig.getInstance().setCustomFragment(true)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }
}