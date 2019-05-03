package com.ltan.music

import android.app.Application
import android.content.Context
import com.ltan.music.common.BaseApplication

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
}