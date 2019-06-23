package com.ltan.music.common

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: BitmapUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-22
 * @Version: 1.0
 */
object BitmapUtil {
    private const val BUFFER_SIZE = 1024 * 10 // 10KB
    private const val COMPRESS_QUALITY = 100

    fun saveTo(bitmap: Bitmap, file: File) {
        val bos = ByteArrayOutputStream(BUFFER_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, bos)
        val fos = FileOutputStream(file)
        bos.writeTo(fos)

        fos.close()
        bos.close()
    }
}