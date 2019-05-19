package com.ltan.music.widget

import android.view.View

/**
 * TMusic.com.ltan.music.widget
 *
 * @ClassName: ListItemViewClickListener
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
interface ListItemViewClickListener {
    /**
     * [v] which view has been clicked
     * [type] as the [v], a useful Integer value
     */
    fun onClick(v: View, type: ClickType)
}