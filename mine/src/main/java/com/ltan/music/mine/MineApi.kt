package com.ltan.music.mine

import com.ltan.music.common.ApiConstants
import com.ltan.music.mine.beans.PlayListRsp
import com.ltan.music.mine.beans.SongSubCunt
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
}