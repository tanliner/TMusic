package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: MMusic
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class MMusic {
    private var name: String? = null

    private var id: Int = 0

    private var size: Int = 0

    private var extension: String? = null

    private var sr: Int = 0

    private var dfsId: Int = 0

    private var bitrate: Int = 0

    private var playTime: Int = 0

    private var volumeDelta: Double = 0.toDouble()

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String? {
        return this.name
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getId(): Int {
        return this.id
    }

    fun setSize(size: Int) {
        this.size = size
    }

    fun getSize(): Int {
        return this.size
    }

    fun setExtension(extension: String) {
        this.extension = extension
    }

    fun getExtension(): String? {
        return this.extension
    }

    fun setSr(sr: Int) {
        this.sr = sr
    }

    fun getSr(): Int {
        return this.sr
    }

    fun setDfsId(dfsId: Int) {
        this.dfsId = dfsId
    }

    fun getDfsId(): Int {
        return this.dfsId
    }

    fun setBitrate(bitrate: Int) {
        this.bitrate = bitrate
    }

    fun getBitrate(): Int {
        return this.bitrate
    }

    fun setPlayTime(playTime: Int) {
        this.playTime = playTime
    }

    fun getPlayTime(): Int {
        return this.playTime
    }

    fun setVolumeDelta(volumeDelta: Double) {
        this.volumeDelta = volumeDelta
    }

    fun getVolumeDelta(): Double {
        return this.volumeDelta
    }
}