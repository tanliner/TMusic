package com.ltan.music.common

import java.io.BufferedReader
import java.io.ByteArrayInputStream

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

    fun getCurrentSongLine(obj: LyricsObj, pos: Int): String? {
        if(obj.songTexts.isNullOrEmpty() || obj.songTexts!![0].start > pos) {
            return null
        }
        for (i in 0 until obj.songTexts!!.size) {
            val it = obj.songTexts!![i]
            var itNext = it
            if(i < obj.songTexts!!.size - 1) {
                itNext = obj.songTexts!![i + 1]
            }
            if(it.start <= pos && itNext.start > pos) {
                return it.txt
            }
        }
        return null
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
        // val lyricObj = LyricsObj()
        val lastQuote = line.lastIndexOf(']')
        when {
            line.startsWith(S_TITLE, true) -> lyricObj.title = line.substring(4, lastQuote)
            line.startsWith(S_ARTIST, true) -> lyricObj.artist = line.substring(4, lastQuote)
            line.startsWith(S_ALBUM, true) -> lyricObj.album = line.substring(4, lastQuote)
            line.startsWith(S_UPLOADER, true) -> {
                if(lyricObj.uploader != null) {
                    lyricObj.uploader = "${lyricObj.uploader} / ${line.substring(4, lastQuote)}"
                } else {
                    lyricObj.uploader = line.substring(4, lastQuote)
                }
            }
            line.startsWith(S_OFFSET, true) -> lyricObj.offset = line.substring(4, lastQuote).toLong()
            else -> {
                if (lastQuote >= 9 && line[lastQuote - 6] == ':') {
                    val txtInfo = LyricsLine()
                    txtInfo.start = calStartTimeMillis(line, lastQuote)
                    txtInfo.txt = line.substring(lastQuote + 1, line.length)
                    lyricObj.songTexts?.add(txtInfo)
                }
            }
        }
    }

    /**
     * [str] song text line
     * [index] the last index of ']', just incase the [100:58:45]somg text at this line
     */
    @JvmStatic
    private fun calStartTimeMillis(str: String, index: Int): Long {
        val minute = str.substring(1, index - 6).toInt()
        val second =  str.substring(index - 5, index - 3).toInt()
        val millisecond = str.substring(index - 2, index).toLong()
        return millisecond + second * 1000 + minute * 60 * 1000
    }
}