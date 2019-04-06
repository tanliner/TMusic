package com.ltan.music.index

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.BindView
import butterknife.Unbinder

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

    @BindView(R2.id.index_title)
    lateinit var title: TextView

    private var unBinder : Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        unBinder = ButterKnife.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unBinder?.unbind()
    }
}
