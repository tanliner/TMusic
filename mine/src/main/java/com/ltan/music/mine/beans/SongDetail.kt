package com.ltan.music.mine.beans

import com.google.gson.annotations.SerializedName

/**
 * TMusic.com.ltan.music.mine.beans
 *
 * @ClassName: SongDetail
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-26
 * @Version: 1.0
 */
data class SongDetailRsp(
    val code: Int,
    val privileges: List<Privileges>? = null,
    @SerializedName("songs")
    val tracks: List<Track>? = null
)