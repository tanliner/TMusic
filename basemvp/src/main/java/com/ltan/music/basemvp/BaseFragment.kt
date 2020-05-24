package com.ltan.music.basemvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import me.yokeyword.fragmentation.SupportFragment

/**
 * TMusic.com.ltan.music.index.fragments
 *
 * @ClassName: MusicBaseFragment
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-26
 * @Version: 1.0
 */
abstract class BaseFragment : SupportFragment() {

    private lateinit var unBinder: Unbinder

    abstract fun contentLayout(): Int
    open fun init(view: View) {}
    open fun init(view: View, savedInstanceState: Bundle?) {
        init(view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(contentLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unBinder = ButterKnife.bind(this, view)
        init(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBinder.unbind()
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