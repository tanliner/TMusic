package com.ltan.music

import android.os.Bundle
import com.jaeger.library.StatusBarUtil
import com.ltan.music.basemvp.MusicBaseActivity

class MainActivity : MusicBaseActivity() {

    override fun initLayout(): Int {
        return R.layout.app_activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setColor(this, resources.getColor(R.color.colorPrimary))
    }
}
