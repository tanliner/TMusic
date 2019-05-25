package com.ltan.music.common

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.util.regex.Pattern

/**
 * TMusic.com.ltan.music.common
 *
 * @ClassName: LyricsUtil
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-24
 * @Version: 1.0
 */
object LyricsUtil {

    private const val S_TITLE = "[ti:"
    private const val S_ARTIST = "[ar:"
    private const val S_ALBUM = "[al:"
    private const val S_UPLOADER = "[by:"
    private const val S_OFFSET = "[offset:"

    fun getCurrentSongLine(obj: LyricsObj, pos: Int): LyricPosition {
        if(obj.songTexts.isNullOrEmpty()) {
            return LyricPosition()
        }
        var j = 0
        val positionObj = LyricPosition()
        val size = obj.songTexts!!.size
        for (i in (size - 1) downTo 0) {
            val it = obj.songTexts!![i]
            if(pos >= it.start) {
                positionObj.txt = it.txt.toString()
                j = i
                break
            }
        }
        if(j < size - 1) {
            positionObj.nextDur = obj.songTexts!![j + 1].start - pos
        }
        return positionObj
    }

    @JvmStatic
    fun parseLyricsInfo(lyric: String?): LyricsObj {
        val info = LyricsObj()
        if(lyric.isNullOrEmpty()) {
            return info
        }
        info.songTexts = ArrayList()
        val bis = ByteArrayInputStream(lyric.toByteArray())
        val bf = BufferedReader(bis.bufferedReader())
        var line: String?
        while ((bf.readLine()).apply { line = this } != null) {
            line?.let { putLineToLyric(info, it) }
        }
        return info
    }

    @JvmStatic
    fun putLineToLyric(lyricObj: LyricsObj, line: String) {
        val lastQuote = line.lastIndexOf(']')
        when {
            line.startsWith(S_TITLE, true) -> lyricObj.title = line.substring(S_TITLE.length, lastQuote)
            line.startsWith(S_ARTIST, true) -> lyricObj.artist = line.substring(S_ARTIST.length, lastQuote)
            line.startsWith(S_ALBUM, true) -> lyricObj.album = line.substring(S_ALBUM.length, lastQuote)
            line.startsWith(S_UPLOADER, true) -> {
                if(lyricObj.uploader != null) {
                    lyricObj.uploader = "${lyricObj.uploader} / ${line.substring(S_UPLOADER.length, lastQuote)}"
                } else {
                    lyricObj.uploader = line.substring(4, lastQuote)
                }
            }
            line.startsWith(S_OFFSET, true) -> lyricObj.offset = line.substring(S_OFFSET.length, lastQuote).toLong()
            else -> {
                val start = calStartTimeMillis(lastQuote, line)
                if(start >= 0) {
                    val txtInfo = LyricsLine()
                    txtInfo.start = start
                    txtInfo.txt = line.substring(lastQuote + 1, line.length)
                    lyricObj.songTexts?.add(txtInfo)
                }
            }
        }
    }

    /**
     * [mm:ss:mmm] or [mm:ss:mm]
     * [index] the last index of ']', just incase the [100:58:45]somg text at this line
     * [line] song text line
     */
    @JvmStatic
    private fun calStartTimeMillis(index: Int, line: String): Long {
        if (index < 9) {
            return -1
        }

        // TODO fix the error line.
        // var fixLineStr = line, [0-1:59:20]/[00:56:99]/[01:32.000]
        val matcher = Pattern.compile("\\[(\\d{2,}:\\d{2}\\.\\d{2,3})\\]").matcher(line)
        if(!matcher.find()) {
            MusicLog.e("LyricUtil/calStartTimeMillis", "error line: $line")
            // fixLineStr = fixLine(index, line)
            return -1
        }

        val timeString = line.substring(1, index)
        val lastPoint = timeString.lastIndexOf('.')
        val lastColon = timeString.lastIndexOf(':')
        val millisec = timeString.substring(lastPoint + 1, index - 1).toLong()
        val sec = timeString.substring(lastColon + 1, lastPoint).toInt()
        val minute = timeString.substring(0, lastColon).toInt()
        return (minute * 60 + sec) * 1000 + millisec
    }
}