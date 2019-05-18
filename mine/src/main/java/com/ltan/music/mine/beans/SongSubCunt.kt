package com.ltan.music.mine.beans

/**
 * TMusic.com.ltan.music.mine.beans
 *
 * @ClassName: SongSubCunt
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-18
 * @Version: 1.0
 */
data class SongSubCunt(
    // user keep, not the favorite list count
    val artistCount: Int = 0,

    // http rsp code
    val code: Int = 0,

    val createDjRadioCount: Int = 0,

    val createdPlaylistCount: Int = 0,

    val djRadioCount: Int = 0,

    val mvCount: Int = 0,

    val newProgramCount: Int = 0,

    val subPlaylistCount: Int = 0,

    val programCount: Int = 0
)