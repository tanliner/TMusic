package com.ltan.music.business.common

import com.ltan.music.business.bean.SongDetailRsp
import com.ltan.music.business.bean.SongUrlRsp
import com.ltan.music.common.ApiConstants
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * TMusic.com.ltan.music.common.api
 *
 * @ClassName: CommonApi
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-23
 * @Version: 1.0
 */
interface CommonApi {
    /**
     * http params:
     * { ids: [21212, 3212, 9926571], br: 999000 }
     * br: code rate, 320000=320K 128000=128K
     */
    @FormUrlEncoded
    @POST(ApiConstants.SONG_URL)
    fun getSongUrl(
        @Field("ids") ids: String,
        @Field("br") num: Int = 999000
    ): Flowable<SongUrlRsp>

    /**
     * http params:
     * { ids: [21212, 3212, 9926571], c: [{id:21212},{id:3212},{9926571}] }
     */
    @FormUrlEncoded
    @POST(ApiConstants.SONG_DETAIL)
    fun getSongDetail(
        @Field("ids") ids: String,
        @Field("c") collector: String // collection ?
    ): Flowable<SongDetailRsp>
}