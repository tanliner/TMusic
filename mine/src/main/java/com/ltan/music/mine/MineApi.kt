package com.ltan.music.mine

import com.ltan.music.business.bean.PlayListRsp
import com.ltan.music.common.ApiConstants
import com.ltan.music.mine.beans.PlayListDetailRsp
import com.ltan.music.mine.beans.SongSubCunt
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: MineApi
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-18
 * @Version: 1.0
 */
interface MineApi {
    @POST(ApiConstants.USER_SUB_COUNT)
    fun subCount(): Flowable<SongSubCunt>

    @FormUrlEncoded
    @POST(ApiConstants.USER_PLAY_LIST)
    fun getPlayList(
        @Field("uid") uid: Long,
        @Field("limit") limit: Int = 30,
        @Field("offset") offset: Int = 0
    ): Flowable<PlayListRsp>

    /**
     * weapi/dj/program/{uid}, uid will be sent by {@code @Path}
     */
    @FormUrlEncoded
    @POST(ApiConstants.USER_DJ_RADIO)
    fun getDjRadio(
        @Path("uid") uid: Long,
        @Field("limit") limit: Int = 30,
        @Field("offset") offset: Int = 0
    ): Flowable<PlayListRsp>

    @FormUrlEncoded
    @POST(ApiConstants.SONG_CATEGORY_LIST)
    fun getPlayLisCategory(
        @Path("uid") uid: Long,
        @Field("limit") limit: Int = 30,
        @Field("offset") offset: Int = 0
    ): Flowable<PlayListRsp>

    @FormUrlEncoded
    @POST(ApiConstants.SONG_PLAY_LIST_DETAIL)
    fun getPlayLisDetail(
        @Field("id") id: Long,
        @Field("n") num: Int = 10000,
        @Field("s") lastCollector: Int = 8
    ): Flowable<PlayListDetailRsp>
}