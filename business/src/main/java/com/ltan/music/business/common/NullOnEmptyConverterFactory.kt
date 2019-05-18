package com.ltan.music.business.common

import com.ltan.music.common.MusicLog
import retrofit2.Converter
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @describe :
 * @usage :
 *
 * </p>
 * Created by tanlin on 2019/5/17
 */
class NullOnEmptyConverterFactory : Converter.Factory() {
    companion object {
        const val TAG = "Convert/Empty"
    }

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val delegate = retrofit.nextResponseBodyConverter<Any>(this, type!!, annotations!!)
        return Converter<ResponseBody, Any> { body ->
            MusicLog.v(TAG, "response empty check length: ${body.contentLength()}")
            val any: Any?
            if (body.contentLength() == 0L) {
                val j = JSONObject()
                j.put("code", 200)
                j.put("msg", "empty")
                any = j
            } else {
                any = delegate.convert(body)
            }
            any
        }
    }
}