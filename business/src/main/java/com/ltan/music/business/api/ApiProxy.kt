package com.ltan.music.business.api

import com.ltan.music.common.MusicLog
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: ApiProxy
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class ApiProxy {

    companion object {
        val instance: ApiProxy by lazy (mode =  LazyThreadSafetyMode.SYNCHRONIZED) { ApiProxy() }

        const val TAG = "ApiProxy"
        const val BASE_URL = "https://music.163.com/"
    }

    private var sRetrofit: Retrofit
    private var httpLoggingInterceptor: HttpLoggingInterceptor
    private var requestHeader: Interceptor

    init {
        httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message
            -> MusicLog.d(TAG,"retrofit = $message")
        })

        requestHeader = Interceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("Accept", "*/*")
            builder.addHeader("Accept-Encoding", "gzip,deflate,sdch")
            builder.addHeader("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4")
            builder.addHeader("Connection", "keep-alive")
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
            builder.addHeader("Host", "music.163.com")
            builder.addHeader("Referer", "http://music.163.com")
            builder.addHeader(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
            )
            chain.proceed(builder.build())
        }

        sRetrofit = initRetrofit(BASE_URL)
    }

    fun <T> getApi(service: Class<T>): T {
        return sRetrofit.create(service)
    }


    // val headerInterceptor = Interceptor { chain ->
    //     // val authorization = LocalAccountInfoManager.getInstance().getAuthorization()
    //     // val builder = chain.request().newBuilder()
    //     // if (!TextUtils.isEmpty(authorization)) {
    //     //     builder.addHeader(HEADER_AUTH, authorization)
    //     // }
    //
    //     // builder.addHeader(HEADER_APPID, 3001.toString())
    //     // chain.proceed(builder.build())
    // }

    //只有 网络拦截器环节 才会写入缓存,在有网络的时候 设置缓存时间
    private val rewriteCacheControlInterceptor = Interceptor { chain ->
        val request = chain.request()
        val originalResponse = chain.proceed(request)
        val maxAge = 3 // 在线缓存在1分钟内可读取 单位:秒
        originalResponse.newBuilder()
            .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
            .removeHeader("Cache-Control")
            .header("Cache-Control", "public, max-age=$maxAge")
            .build()
    }


    private fun initRetrofit(baseUri: String): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            // .cache(cache)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(requestHeader)
            // .addInterceptor(baseInterceptor)
            // .addInterceptor(headerInterceptor)
            // .addNetworkInterceptor(rewriteCacheControlInterceptor)
            .callTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUri)
            .build()
    }
}