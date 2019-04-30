package com.ltan.music.basemvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import kotlin.reflect.KProperty

/**
 * TMusic.com.ltan.music.index.fragments
 *
 * @ClassName: MusicBaseFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-26
 * @Version: 1.0
 */
abstract class MusicBaseFragment : Fragment() {

    private var unBinder: Unbinder? = null

    abstract fun initLayout(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(initLayout(), container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unBinder = ButterKnife.bind(this, view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBinder?.unbind()
    }
}
// for butterknife in Kotlin
operator fun Any.setValue(fragment: Fragment, property: KProperty<*>, v: View) {

}