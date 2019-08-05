package com.ltan.music.common.song

/**
 * TMusic.com.ltan.music.common.song
 *
 * @ClassName: SongUtils
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-23
 * @Version: 1.0
 */
object SongUtils {
    fun buildArgs(vararg ids: Long): String {
        return buildArgs(ids.toTypedArray())
    }

    fun buildArgs(songIds: Array<Long>): String {
        val idsBuilder = StringBuilder()
        idsBuilder.append("[")
        songIds.forEach {
            idsBuilder.append(it).append(",")
        }
        idsBuilder.deleteCharAt(idsBuilder.length - 1)
        idsBuilder.append("]")
        return idsBuilder.toString()
    }

    fun buildCollectors(vararg songIds: Long): String {
        return buildCollectors(songIds.toTypedArray())
    }

    fun buildCollectors(songIds: Array<Long>): String {
        val collectorBuilder = StringBuilder()
        collectorBuilder.append("[")
        songIds.forEach {
            collectorBuilder.append("{\"id\":").append(it).append("},")
        }
        collectorBuilder.deleteCharAt(collectorBuilder.length - 1)
        collectorBuilder.append("]")
        return collectorBuilder.toString()
    }

    /**
     * find out the last index by mode [size]
     */
    fun getLastIndex(cur: Int, size: Int): Int {
        var last = cur - 1
        if (size > 0) {
            var next = cur - 1 // circle
            if (next < 0) {
                next = size - 1
            }
            last = next % size
        }
        return last
    }

    /**
     * find out the next index by mode [size]
     */
    fun getNextIndex(cur: Int, size: Int): Int {
        var next = cur + 1 // circle
        next %= size
        return next
    }
}