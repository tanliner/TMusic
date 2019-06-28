package com.ltan.music.common

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: LyricsRsp
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-23
 * @Version: 1.0
 */
data class LyricsRsp(
    val sgc: Boolean = false,

    val sfy: Boolean = false,

    val qfy: Boolean = false,

    val lyricUser: LyricUser? = null,

    val lrc: Lrc? = null,

    val klyric: Lrc? = null,

    val tlyric: Lrc? = null,

    val code: Int = 0
)

data class LyricUser(
    val id: Long = 0,

    val status: Int = 0,

    val demand: Int = 0,

    val userid: Long = 0,

    val nickname: String? = null,

    val uptime: Long = 0
)

data class Lrc(
    val version: Int = 0,

    val lyric: String? = null
)

data class LyricsObj (
    var title: String? = null,
    var artist: String? = null,
    var album: String? = null,
    var uploader: String? = null,
    var offset: Long = 0,
    var songTexts: MutableList<LyricsLine>? = null
)

data class LyricsLine(
    var start: Long = 0,
    var txt: String = ""
)