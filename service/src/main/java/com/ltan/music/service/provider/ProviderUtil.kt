package com.ltan.music.service.provider

import android.net.Uri

/**
 * TMusic.com.ltan.music.service.provider
 *
 * @ClassName: ProviderUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-09-03
 * @Version: 1.0
 */
object ProviderUtil {
    @JvmStatic
    val USER_URI = Uri.withAppendedPath(MusicProvider.BASE_URI, "users")

    @JvmStatic
    val SONG_UNAVAILABLE_URI = Uri.withAppendedPath(MusicProvider.BASE_URI, "song_unavailable")
}