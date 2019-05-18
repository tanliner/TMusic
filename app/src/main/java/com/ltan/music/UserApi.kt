package com.ltan.music

import com.ltan.music.account.beans.LoginResult
import com.ltan.music.business.api.Module
import com.ltan.music.business.api.Root
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
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
        @Field("rememberLogin") remember: Boolean = true
    ): Flowable<LoginResult>

    @POST("weapi/logout")
    fun logout(): Flowable<Any?>

    @GET("api/radio/get")
    fun query() : Flowable<String>

    @GET("json/")
    fun test() : Flowable<Module<List<Root>>>
}