package com.ltan.music.service.blur

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/24
 * @version 1.0
 */
class BlurRequestListener(cb: ResourceReady) : RequestListener<Drawable> {
    private val callback: ResourceReady = cb
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        callback.onFailed()
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        callback.onReady(resource)
        return true
    }
}