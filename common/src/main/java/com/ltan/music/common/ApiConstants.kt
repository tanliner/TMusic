package com.ltan.music.common

/**
 * TMusic.com.ltan.music
 *
 * @ClassName: ApiConstants
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-18
 * @Version: 1.0
 */
object ApiConstants {
    const val CRYPTO_WEAPI = "weapi"
    const val CRYPTO_LINUX_API = "linuxapi"
    const val CRYPTO_EAPI = "eapi"
    const val CRYPTO_KEY = "crypto"

    // user
    const val USER_LOGIN = "weapi/login"
    const val USER_LOGOUT = "weapi/logout"
    const val USER_DETAIL = "weapi/v1/user/detail/{uid}"
    // {uid, limit, offset}, default: limit=30, offset=0
    const val USER_PLAY_LIST = "weapi/user/playlist"
    // {uid, type}, values: type=1, one week ago; type=0, all of it
    const val USER_RECORD = "weapi/v1/play/record"
    // use cookie
    const val USER_SUB_COUNT = "weapi/subcount"
    // user DJ radio
    const val USER_DJ_RADIO = "weapi/dj/program/{uid}"
    //
    const val USER_UPDATE = "weapi/user/profile/update"

    /* song */
    // param: {ids:[id2, id3, ...], br: int || 999000}
    const val SONG_URL = "weapi/song/enhance/player/url"
    // Note: linuxapi
    const val SONG_LYRICS = "weapi/song/lyric?lv=-1&kv=-1&tv=-1"
    // param: {c: [{id1}, {id2}]} or {ids: [id1, ...]}
    const val SONG_DETAIL = "weapi/v3/song/detail"
    /* play list category: app->discovery->song_list->all */
    const val SONG_CATEGORY_LIST = "weapi/playlist/catalogue"
    /* play list category: app->discovery->song_list->all */
    const val SONG_PLAY_LIST_DETAIL = "weapi/v3/playlist/detail"
}