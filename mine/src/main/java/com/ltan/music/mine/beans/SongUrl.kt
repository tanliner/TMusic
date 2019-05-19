package com.ltan.music.mine.beans

/**
 * TMusic.com.ltan.music.mine.beans
 *
 * @ClassName: SongUrl
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
data class SongUrlRsp(
    val code: Int = 0,
    val data: List<SongUrl>? = null
)

data class SongUrl(

    val br: Long = 0,

    val canExtend: Boolean = false,

    val code: Int = 0,

    val encodeType: String? = null,

    val expi: Int = 0,

    val fee: Int = 0,

    val flag: Int = 0,

    val freeTrialInfo: String? = null,

    val gain: Double = 0.toDouble(),

    val id: Long = 0,

    val level: String? = null,

    val md5: String? = null,

    val payed: Int = 0,

    val size: Long = 0,

    val type: String? = null,

    val uf: String? = null,

    val url: String? = null
)
