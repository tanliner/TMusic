package com.ltan.music.business.common

import com.ltan.music.common.MusicLog
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * TMusic.com.ltan.music.business.common
 *
 * http response may be empty
 * 1. let the server write something out
 * 2. inject a empty obj via converter
 *
 * @ClassName: NullOnEmptyConverterFactory
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-17
 * @Version: 1.0
 */
class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val delegate = retrofit.nextResponseBodyConverter<Any>(this, type!!, annotations!!)
        return Converter<ResponseBody, Any> { body ->
            MusicLog.v("response empty check length: ${body.contentLength()}")
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