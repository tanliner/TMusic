package com.ltan.music.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element.U8_4
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View


/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: EasyBlur
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-08
 * @Version: 1.0
 */
class EasyBlur private constructor() {

    private lateinit var mCtx: Context
    private lateinit var mBitmap: Bitmap
    private var mRadius = 0
    private var mScale = SCALE
    private var mAlgorithm = BlurAlgorithm.RS_BLUR // default RenderScript

    companion object {
        const val SCALE = 1 / 8.0F //default scale
        const val MAX_RADIUS = 25
        val sInstance: EasyBlur by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { EasyBlur() }

        fun rsBlur(ctx: Context, source: Bitmap, radius: Int, scale: Float): Bitmap {
            var width = Math.round(source.width * scale)
            var height = Math.round(source.height * scale)

            if(width <= 2) {
                width = 2
            }
            if(height <= 2) {
                height = 2
            }

            val outBitmap = Bitmap.createScaledBitmap(source, width, height, false)

            val renderScript = RenderScript.create(ctx)

            MusicLog.d("out scale size: ${outBitmap.width} * ${outBitmap.height}")

            // Allocate memory for Renderscript to work with

            val input = Allocation.createFromBitmap(renderScript, outBitmap)
            val output = Allocation.createTyped(renderScript, input.type)

            // Load up an instance of the specific script that we want to use.
            val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, U8_4(renderScript))
            scriptIntrinsicBlur.setInput(input)

            // Set the blur radius
            scriptIntrinsicBlur.setRadius(radius.toFloat())

            // Start the ScriptIntrinisicBlur
            scriptIntrinsicBlur.forEach(output)

            // Copy the output to the blurred bitmap
            output.copyTo(outBitmap)

            renderScript.destroy()
            return outBitmap
        }

        fun fastBlur() {
            // Glide.with().transform(BlurTransformation(28, 35)).into(imgView)
        }
    }

    enum class BlurAlgorithm {
        RS_BLUR,
        FAST_BLUR
    }

    fun with(ctx: Context): EasyBlur {
        mCtx = ctx.applicationContext
        return this
    }

    fun bitmap(map: Bitmap): EasyBlur {
        mBitmap = map
        return this
    }

    fun radius(r: Int): EasyBlur {
        mRadius = r
        if(mRadius > MAX_RADIUS) {
            mRadius = MAX_RADIUS
        }
        return this
    }

    fun scale(scale: Int): EasyBlur {
        mScale = 1.0F / scale
        return this
    }

    fun into(view: View) {
        view.post {
            view.background = BitmapDrawable(mCtx.resources, blur())
        }
    }

    fun algorithm(algorithm: BlurAlgorithm): EasyBlur {
        mAlgorithm = algorithm
        return this
    }

    // the render script solution
    fun blur(): Bitmap {
        if (mAlgorithm != BlurAlgorithm.RS_BLUR || Build.VERSION.SDK_INT <= 8) {
            throw IllegalArgumentException("mAlgorithm must be renderscript, and sdk greater than 9")
        }
        return rsBlur(mCtx, mBitmap, mRadius, mScale)
    }
}