package com.ltan.music.service.blur

import android.graphics.drawable.Drawable

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/24
 * @version 1.0
 */
interface ResourceReady {
    fun onFailed() {} /* default method */
    fun onReady(resource: Drawable?)
}