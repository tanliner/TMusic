package com.ltan.music.service.blur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.ltan.music.basemvp.RxUtils
import com.ltan.music.common.EasyBlur

/**
 *
 * @desc
 * Blur the whole layout background when resource ready
 * @author  tanlin
 * @since  2020/5/24
 * @version 1.0
 */
class ImageBlur constructor(
    appCtx: Context, target: ImageView,
    private val radius: Int = 25,
    private val sample: Int = 10
) : ResourceReady {
    private val ctx = appCtx
    private val mPreviewBg = target

    override fun onReady(resource: Drawable?) {
        RxUtils.async(object : RxUtils.Work<Bitmap> {
            override fun get(): Bitmap {
                val bitmap: Bitmap
                bitmap = if (resource is BitmapDrawable) {
                    resource.bitmap;
                } else {
                    val newBitmap = Bitmap.createBitmap(
                        mPreviewBg.width,
                        mPreviewBg.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val c = Canvas(newBitmap)
                    resource?.draw(c)
                    newBitmap
                }
                // val result2 = FastBlur.blur(bitmap, 30, true)
                return EasyBlur.sInstance
                    .with(ctx)
                    .bitmap(bitmap)
                    .radius(radius)
                    .scale(sample)
                    .blur()
            }
        }, object : RxUtils.Main<Bitmap> {
            override fun doOnMain(t: Bitmap) {
                mPreviewBg.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        mPreviewBg.setImageBitmap(t)
                        mPreviewBg.viewTreeObserver.removeOnPreDrawListener(this)
                        return false
                    }
                })
            }
        })
    }
}