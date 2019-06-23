package com.ltan.music.service.api

import com.ltan.music.common.ApiConstants
import com.ltan.music.common.LyricsRsp
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * TMusic.com.ltan.music.service
 *
 * @ClassName: MusicServiceApi
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-26
 * @Version: 1.0
 */
interface MusicServiceApi {

    /**
     * get lyric for the special song by [id] indicated
     */
    @FormUrlEncoded
    @POST(ApiConstants.SONG_LYRICS)
    fun getLyrics(
        @Field("id") id: String,
        @Field(ApiConstants.CRYPTO_KEY) api: String = ApiConstants.CRYPTO_LINUX_API
    ): Flowable<LyricsRsp>
}