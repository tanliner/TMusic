package com.ltan.music.service

/**
 * TMusic.com.ltan.music.service
 *
 * to tell the client, which song is playing
 *
 * @ClassName: SongPlaying
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-22
 * @Version: 1.0
 */
data class SongPlaying(
    var id: Long = 0,
    var url: String,
    var title: String? = null,
    var subtitle: String? = null
)