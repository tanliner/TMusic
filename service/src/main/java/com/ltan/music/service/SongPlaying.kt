package com.ltan.music.service

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
@Parcelize
data class SongPlaying(
    var id: Long = 0,
    var url: String,
    // ImageUrl for preview during playing, load image with `Glide`
    var picUrl: String? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var artists: String = ""
): Parcelable