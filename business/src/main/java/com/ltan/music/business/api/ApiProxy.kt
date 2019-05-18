package com.ltan.music.business.api

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.ltan.music.business.common.NullOnEmptyConverterFactory
import com.ltan.music.business.common.ReqAgents
import com.ltan.music.common.BaseApplication
import com.ltan.music.common.Constants
import com.ltan.music.common.Encryptor
import com.ltan.music.common.MusicLog
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: ApiProxy
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class ApiProxy private constructor() {

    companion object {
        // singleton
        val instance: ApiProxy by lazy (mode =  LazyThreadSafetyMode.SYNCHRONIZED) { ApiProxy() }

        private val TAG = ApiProxy::class.java.simpleName
        private val TYPE = arrayOf("pc", "mobile")

        const val BASE_URL = "https://music.163.com/"
        // const val BASE_URL = "http://192.168.201.2:9090/"
        const val HEADER_INIT = "_ga=GA1.1.1970532667.1556805403; os=pc"
        const val HEADER_COOKIE = "Cookie"
        const val HEADER_SET_COOKIE = "Set-Cookie"
        const val BODY_QUERY_CSRF = "csrf_token"
        const val HEADER_COOKIE_CSRF_PATTERN = "/_csrf =([^(;|\$)]+)/"
    }

    private var sRetrofit: Retrofit
    private var httpLoggingInterceptor: HttpLoggingInterceptor
    private var requestHeader: Interceptor

    init {
        httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message
            -> MusicLog.v(TAG,"retrofit = $message")
        })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        requestHeader = Interceptor { chain ->
            val builder = chain.request().newBuilder()
            val index = (Math.random() * 2).toInt()
            val agent = ReqAgents.chooseUserAgent(TYPE[index])
            // val agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"

            builder.addHeader("Accept", "*/*")
            // response gzip encoded
            // builder.addHeader("Accept-Encoding", "gzip,deflate,sdch,br")
            builder.addHeader("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4")
            builder.addHeader("Connection", "keep-alive")
            builder.addHeader("Host", "music.163.com")
            builder.addHeader("Referer", "https://music.163.com")
            builder.addHeader("User-Agent", agent)
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
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

        val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(BaseApplication.getAPPContext()))

        val okHttpClient = OkHttpClient.Builder()
            // .cache(cache)
            .addInterceptor(requestHeader)
            .addInterceptor(AddCookiesInterceptor()) // cookies
            .addInterceptor(CSRTokenInterceptor()) // params
            .addInterceptor(ReqBodyModifyInterceptor()) // body modify
            // .addInterceptor(baseInterceptor)
            // .addInterceptor(headerInterceptor)
            // .addNetworkInterceptor(rewriteCacheControlInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(NullOnEmptyConverterFactory()) /* ahead of other convert */
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUri)
            .build()
    }

    class AddCookiesInterceptor : Interceptor {

        companion object {
            val TAG = "${ApiProxy.TAG}/${AddCookiesInterceptor::class.java.simpleName}"
        }
        private var baseCookie: String

        init {
            val jsessionid = Encryptor.randomStr(Encryptor.BASE64, 176) + ":${Date().time}"
            val nuid = Encryptor.randomStr(Encryptor.BASE36, 32)
            baseCookie = "JSESSIONID-WYYY=$jsessionid; _iuqxldmzr_=32; _ntes_nnid=$nuid,${Date().time}; _ntes_nuid=$nuid"
        }

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            val oldRequest = chain.request()
            val appContext: Context = BaseApplication.getAPPContext()
            val builder = oldRequest.newBuilder()
            val cookies = HashSet<String>()
            val preferences = appContext.getSharedPreferences(Constants.LOCAL_SP_COOKIE_NAME, Context.MODE_PRIVATE)
                .getStringSet(Constants.LOCAL_SP_COOKIE_SECTION, cookies) as HashSet
            if (preferences.isEmpty()) {
                preferences.add(HEADER_INIT)
            } else {
                // in case cheating check.
                // preferences.add(baseCookie)
            }

            if (preferences.isNotEmpty()) {
                for (cookie in preferences) {
                    builder.addHeader(HEADER_COOKIE, cookie)
                }
            }
            return chain.proceed(builder.build())
        }
    }

    class CSRTokenInterceptor : Interceptor {
        companion object {
            val TAG = "${ApiProxy.TAG}/${CSRTokenInterceptor::class.java.simpleName}"
        }
        override fun intercept(chain: Interceptor.Chain): Response {
            val oldRequest = chain.request()
            val request: Request

            val headers = oldRequest.headers()
            if (headers.size() > 0) {
                val cookie = headers.get(HEADER_COOKIE)
                val hasToken = cookie?.matches(Regex(HEADER_COOKIE_CSRF_PATTERN))
                MusicLog.d(TAG, "CSRTokenInterceptor cookies: $cookie")
                request = if (hasToken != null) {
                    addParam(oldRequest, BODY_QUERY_CSRF, cookie.split(";")[1])
                } else {
                    addParam(oldRequest, BODY_QUERY_CSRF, "")
                }
            } else {
                request = oldRequest
            }

            return chain.proceed(request)
        }

        private fun addParam(request: Request, name: String, value: String): Request {

            var newRequest: Request = request
            val method = request.method().toUpperCase()
            if(method == "POST") {
                val bodyBuilder = FormBody.Builder()
                if(request.body() is FormBody) {
                    val oldFormBody = request.body() as FormBody
                    for (i in 0 until oldFormBody.size()) {
                        // oldParam.addProperty(oldFormBody.encodedName(i), oldFormBody.encodedValue(i))
                        bodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i))
                    }
                    bodyBuilder.add(name, value)
                    newRequest = request.newBuilder()
                        .post(bodyBuilder.build())
                        .build()
                }

            } else {
                val builder = request.url().newBuilder()
                    // .setEncodedQueryParameter(name, value)
                    .setQueryParameter(name, value)
                newRequest = request.newBuilder()
                    // .method(request.method(), request.body())
                    .url(builder.build())
                    .build()
            }

            return newRequest
        }
    }

    class ReqBodyModifyInterceptor : Interceptor {
        companion object {
            val TAG = "${ApiProxy.TAG}/${ReqBodyModifyInterceptor::class.java.simpleName}"
        }
        override fun intercept(chain: Interceptor.Chain): Response {
            val oldRequest = chain.request()
            val method = oldRequest.method().toUpperCase()
            val url = oldRequest.url()

            val newRequest: Request
            newRequest = if ("GET" == method) {
                oldRequest.newBuilder().url(selectGetParams(url)).build()
            } else {
                selectPostParams(oldRequest)
            }
            return chain.proceed(newRequest)
        }

        private fun selectGetParams(url: HttpUrl): HttpUrl {
            val json = JsonObject()
            for (i in 0 until url.querySize()) {
                val key = url.queryParameterName(i)
                val value = url.queryParameterValue(i)

                json.addProperty(key, value)
            }
            MusicLog.d(TAG, "selectGetParams json to string: $json")

            // random key, len = 16
            val secretKey = Encryptor.randomBytes(16)

            // encrypt twice
            val encTextPre = Encryptor.encrypt(Encryptor.strToByteArray(json.toString()), Encryptor.KEY)
            val paramsTxt = Encryptor.encrypt(encTextPre, secretKey)

            val encSecKey = Encryptor.rsaEncrypt(secretKey, Encryptor.PUBLIC_KEY)

            val newBuilder = url.newBuilder()
            newBuilder.addQueryParameter("params", paramsTxt)
            newBuilder.addQueryParameter("encSecKey", encSecKey)
            return newBuilder.build()
        }

        private fun selectPostParams(request: Request): Request {
            val json = JsonObject()
            MusicLog.d(TAG, "request body: ${request.body()}")
            if (request.body() is FormBody) {
                val oldFormBody = request.body() as FormBody
                for (i in 0 until oldFormBody.size()) {
                    json.addProperty(oldFormBody.name(i), oldFormBody.value(i))
                }
                MusicLog.d(TAG, "params json string: $json")
            }
            return encryptParam(request, json)

        }

        private fun encryptParam(request: Request, json: JsonObject): Request {
            // random key, len = 16
            val secretKey = Encryptor.randomBytes(16)

            // encrypt twice
            val encTextPre = Encryptor.encrypt(json.toString(), Encryptor.KEY)
            val paramsTxt = Encryptor.encrypt(encTextPre, secretKey)
            secretKey.reverse()
            val encSecKey = Encryptor.rsaEncrypt(secretKey, Encryptor.PUBLIC_KEY)

            val builder = FormBody.Builder()
            builder.add("params", paramsTxt)
            builder.add("encSecKey", encSecKey)
            return request.newBuilder()
                .post(builder.build())
                .build()
        }
    }
}