package com.ltan.music.common.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongItemObject
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
@Parcelize
data class SongItemObject(
    var number: Int = 0,
    var title: String? = null,
    var songId: Long = 0,
    var subTitle: String? = null,
    var artists: String = "",
    var video: Boolean = false,
    var songUrl: String? = null,
    var picUrl: String? = null
) : Parcelable