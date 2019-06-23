package com.ltan.music.common.song

/**
 * TMusic.com.ltan.music.common.song
 *
 * @ClassName: ReqArgs
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-23
 * @Version: 1.0
 */
object ReqArgs {
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
}