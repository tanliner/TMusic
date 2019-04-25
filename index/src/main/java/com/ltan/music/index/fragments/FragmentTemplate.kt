package com.ltan.music.index.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * TMusic.com.ltan.music.index.fragments
 *
 * @ClassName: FragmentTemplate
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-26
 * @Version: 1.0
 */
class FragmentTemplate : Fragment() {

    companion object {
        const val FRG_KEY = "fragId"

        fun newInstance(fragId: Int): FragmentTemplate {
            val args = Bundle()
            args.putInt(FRG_KEY, fragId)
            val ft = FragmentTemplate()
            ft.arguments = args
            return ft
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = arguments?.getInt(FRG_KEY)?.let { inflater.inflate(it, container, false) }
        if(view == null) {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}