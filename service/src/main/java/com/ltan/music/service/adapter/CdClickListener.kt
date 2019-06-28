package com.ltan.music.service.adapter

/**
 * TMusic.com.ltan.music.service.adapter
 *
 * @ClassName: CdClickListener
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-28
 * @Version: 1.0
 */
interface CdClickListener {
    fun onLongClick(): Boolean
    fun onClick()
}