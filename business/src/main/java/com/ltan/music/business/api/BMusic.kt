package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: BMusic
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
data class BMusic(
    var name: String? = null,

    var id: Int = 0,

    var size: Int = 0,

    var extension: String? = null,

    var sr: Int = 0,

    var dfsId: Int = 0,

    var bitrate: Int = 0,

    var playTime: Int = 0,

    var volumeDelta: Double = 0.toDouble()
) {
    override fun toString(): String {
        return "BMusic@${this.hashCode()}:{"+
                "name: $name\nid: $id\nsize: $size\nextension: $extension\nsr: $sr\ndfsId: $dfsId" +
                "bitrate: $bitrate\nplayTime: $playTime\nvolumeDelta: $volumeDelta" +
                "}"
    }
}