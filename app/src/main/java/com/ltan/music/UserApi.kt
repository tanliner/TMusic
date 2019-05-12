package com.ltan.music

import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * TMusic.com.ltan.music
 *
 * @ClassName: UserApi
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
interface UserApi {

    @FormUrlEncoded
    @POST("weapi/login")
    fun login(
        @Field("username") name: String,
        @Field("password") password: String,
        @Field("rememberLogin") remember: Boolean
    ): Flowable<String>
}