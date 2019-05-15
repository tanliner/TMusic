package com.ltan.music.business.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.ltan.music.business.common.ReqAgents
import com.ltan.music.common.BaseApplication
import com.ltan.music.common.Encryptor
import com.ltan.music.common.MusicLog
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
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
        // const val BASE_URL = "http://192.168.201.2:9090/"
        val tt = "{\"popAdjust\":false,\"data\":[{\"name\":\"可惜没如果\",\"id\":29814898,\"position\":7,\"alias\":[\"If Only…\"],\"status\":0,\"fee\":8,\"copyrightId\":7002,\"disc\":\"1\",\"no\":7,\"artists\":[{\"name\":\"林俊杰\",\"id\":3684,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}],\"album\":{\"name\":\"新地球\",\"id\":3056951,\"type\":\"专辑\",\"size\":13,\"picId\":3238061746556733,\"blurPicUrl\":\"http://p2.music.126.net/X0EDfXzxMQJiQ-71JFGdZw==/3238061746556733.jpg\",\"companyId\":0,\"pic\":3238061746556733,\"picUrl\":\"http://p2.music.126.net/X0EDfXzxMQJiQ-71JFGdZw==/3238061746556733.jpg\",\"publishTime\":1419609600007,\"description\":\"\",\"tags\":\"\",\"company\":\"华纳唱片\",\"briefDesc\":\"\",\"artist\":{\"name\":\"\",\"id\":0,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0},\"songs\":[],\"alias\":[],\"status\":3,\"copyrightId\":7002,\"commentThreadId\":\"R_AL_3_3056951\",\"artists\":[{\"name\":\"林俊杰\",\"id\":3684,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}],\"subType\":\"录音室版\",\"transName\":null},\"starred\":false,\"popularity\":100.0,\"score\":100,\"starredNum\":0,\"duration\":298276,\"playedNum\":0,\"dayPlays\":0,\"hearTime\":0,\"ringtone\":null,\"crbt\":null,\"audition\":null,\"copyFrom\":\"\",\"commentThreadId\":\"R_SO_4_29814898\",\"rtUrl\":null,\"ftype\":0,\"rtUrls\":[],\"copyright\":1,\"transName\":null,\"sign\":null,\"hMusic\":{\"name\":null,\"id\":1186556209,\"size\":11933822,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":320000,\"playTime\":298276,\"volumeDelta\":-37800.0},\"mMusic\":{\"name\":null,\"id\":1186556210,\"size\":7160310,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":192000,\"playTime\":298276,\"volumeDelta\":-35300.0},\"lMusic\":{\"name\":null,\"id\":1186556211,\"size\":4773554,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":128000,\"playTime\":298276,\"volumeDelta\":-33500.0},\"bMusic\":{\"name\":null,\"id\":1186556211,\"size\":4773554,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":128000,\"playTime\":298276,\"volumeDelta\":-33500.0},\"rtype\":0,\"rurl\":null,\"mvid\":375130,\"mp3Url\":null,\"alg\":\"default\"}],\"code\":200}"
    }

    private var sRetrofit: Retrofit
    private var httpLoggingInterceptor: HttpLoggingInterceptor
    private var requestHeader: Interceptor

    init {
        httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message
            -> MusicLog.v(TAG,"retrofit = $message")
        })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val type = arrayOf("pc", "mobile")
        requestHeader = Interceptor { chain ->
            val builder = chain.request().newBuilder()
            val index = (Math.random() * 2).toInt()
            val agent = ReqAgents.chooseUserAgent(type[index])

            builder.addHeader("Accept", "*/*")
            builder.addHeader("Accept-Encoding", "gzip,deflate,sdch,br")
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
        val okHttpClient = OkHttpClient.Builder()
            // .cache(cache)
            .addInterceptor(requestHeader)
            .addInterceptor(AddCookiesInterceptor()) // cookies
            .addInterceptor(ReceivedCookiesInterceptor()) // cookies
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
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            // .addConverterFactory(MyConvertFac())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUri)
            .build()
    }

    class ReceivedCookiesInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse = chain.proceed(chain.request())

            if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
                val cookies = HashSet<String>()

                for (header in originalResponse.headers("Set-Cookie")) {
                    cookies.add(header)
                }

                val appContext: Context = BaseApplication.getAPPContext()
                val config =
                appContext.getSharedPreferences("config", Context.MODE_PRIVATE)
                        .edit()
                config.putStringSet("cookie", cookies)
                config.commit()
            }
            return originalResponse
        }
    }

    class AddCookiesInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            val appContext: Context = BaseApplication.getAPPContext()
            val builder = chain.request().newBuilder()
            val cookies = HashSet<String>()
            val preferences = appContext.getSharedPreferences("config", Context.MODE_PRIVATE)
                .getStringSet("cookie", cookies) as HashSet
            if (preferences.isEmpty()) {
                for (cookie in preferences) {
                    builder.addHeader("Cookie", cookie)
                    // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
                    MusicLog.v("OkHttp", "Adding Header: $cookie")
                }
            }
            return chain.proceed(builder.build())
        }
    }

    class CSRTokenInterceptor : Interceptor {
        companion object {
            const val TAG = ApiProxy.TAG + "/CSRToken"
        }
        override fun intercept(chain: Interceptor.Chain): Response {
            val oldRequest = chain.request()
            var midRequest: Request = oldRequest

            val headers = oldRequest.headers()
            if(headers.size() > 0) {
                val cookie = headers.get("Cookie")
                val hasToken = cookie?.matches(Regex("/_csrf =([^(;|\$)]+)/"))
                MusicLog.d(TAG, "CSRTokenInterceptor cookies: $cookie")
                if (hasToken != null) {
                    // url.q
                    midRequest = addParam(oldRequest, "csrf_token", cookie.split(";")[1])
                } else {
                    midRequest = addParam(oldRequest, "csrf_token", "")
                    val oldFormBody = midRequest.body() as FormBody
                    for (i in 0 until oldFormBody.size()) {
                        MusicLog.d(TAG, "CSRTokenInterceptor body $i: ${oldFormBody.value(i)}")
                    }
                }
            } else {
                midRequest = oldRequest
            }

            return chain.proceed(midRequest)
            // val csrfToken = (headers['Cookie'] || '').match()
            // data.csrf_token = csrfToken ? csrfToken[1] : ''
            // data = encrypt.weapi(data)
            // url = url.replace(/\w*api/, 'weapi')
        }

        private fun addParam(request: Request, name: String, value: String): Request {

            val newRequest: Request
            val method = request.method().toUpperCase()
            if(method == "POST") {
                val bodyBuilder = FormBody.Builder()
                val oldFormBody = request.body() as FormBody
                for (i in 0 until oldFormBody.size()) {
                    // oldParam.addProperty(oldFormBody.encodedName(i), oldFormBody.encodedValue(i))
                    bodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i))
                }
                bodyBuilder.add(name, value)
                newRequest = request.newBuilder()
                    .post(bodyBuilder.build())
                    .build()

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
            const val TAG = ApiProxy.TAG + "/ReqBodyModify"
        }
        override fun intercept(chain: Interceptor.Chain): Response {
            val oldRequest = chain.request()
            val method = oldRequest.method().toUpperCase()
            val url = oldRequest.url()

            val finalRequest: Request
            finalRequest = if ("GET" == method) {
                oldRequest.newBuilder().url(selectGetParams(url)).build()
            } else {
                selectPostParams(url, oldRequest)
            }
            return chain.proceed(finalRequest)
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

        private fun selectPostParams(url: HttpUrl, request: Request): Request {
            var json = JsonObject()

            var newRequest: Request = request
            if (request.body() is FormBody) {
                val oldFormBody = request.body() as FormBody

                for (i in 0 until oldFormBody.size()) {
                    json.addProperty(oldFormBody.name(i), oldFormBody.value(i))
                }

                MusicLog.d(TAG, "params json string: $json")
                // random key, len = 16
                // val secretKey = Encryptor.randomBytes(16)
                val secretKey = byteArrayOf(0x68, 0x6c, 0x50, 0x75, 0x6a, 0x34, 0x65, 0x76, 0x59, 0x6f, 0x6e, 0x49, 0x6a, 0x32, 0x34, 0x52)

                // json = JsonObject()
                // json.addProperty("username", "test@163.com")
                // json.addProperty("password", "test_password")
                var txt = json.toString()
                // txt = "{\"aaa\":\"test@163.com\",\"bbb\":\"test_password\"}"

                // encrypt twice
                val encTextPre = Encryptor.encrypt(txt, Encryptor.KEY)
                val paramsTxt = Encryptor.encrypt(encTextPre, secretKey)
                MusicLog.d(TAG, "first encText... ${encTextPre}")
                MusicLog.d(TAG, "second encText... ${paramsTxt}")

                val encSecKey = Encryptor.rsaEncrypt(secretKey.reverse(), Encryptor.PUBLIC_KEY)
                // val encSecKey = Encryptor.rsaEncrypt(secretKey, Encryptor.getPublicKey().toString())

                val builder = FormBody.Builder()
                builder.add("params", paramsTxt)
                builder.add("encSecKey", encSecKey)
                // val rBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), bodyToString(builder.build()))
                MusicLog.d(TAG, "new param json string: ${bodyToString(builder.build())}")
                newRequest = request
                    .newBuilder()
                    .post(builder.build())
                    .build()
            }

            return newRequest

        }

        fun bodyToString(body: FormBody): String {
            val json = JsonObject()
            for (i in 0 until body.size()) {
                json.addProperty(body.encodedName(i), body.encodedValue(i))
            }
            return json.toString()
        }
    }

    object AA {
        // const crypto = require('crypto')
        // const iv = Buffer.from('0102030405060708')
        // const presetKey = Buffer.from('0CoJUm6Qyw8W8jud')
        // const linuxapiKey = Buffer.from('rFgB&h#%2?^eDg:Q')
        // const base62 = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
        // const publicKey = '-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB\n-----END PUBLIC KEY-----'

    }
}