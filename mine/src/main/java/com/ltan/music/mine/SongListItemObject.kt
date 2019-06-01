package com.ltan.music.mine

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongListItemObject
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-08
 * @Version: 1.0
 */
@Parcelize
data class SongListItemObject (
    val songId: Long,
    val imgId: Int = 0,
    var imgUrl: String? = null,
    val title: String? = "",
    val count: Int = 0,
    var owner: String? = "",
    var isHeartMode: Boolean = false
) : Parcelable