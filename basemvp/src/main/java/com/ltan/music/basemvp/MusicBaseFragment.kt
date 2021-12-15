package com.ltan.music.basemvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

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

    companion object {
        var TAG: String = this::class.java.simpleName
    }


    abstract fun initLayout(): Int
    open fun init(view: View) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(initLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    open fun onBack() {
        val manager = fragmentManager ?: return
        if (manager.fragments.size == 1) {
            // call activity finish
            activity?.finish()
        } else {
            manager.popBackStack()
        }
    }
}