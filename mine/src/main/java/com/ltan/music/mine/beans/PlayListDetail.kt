package com.ltan.music.mine.beans

import com.ltan.music.business.bean.PlayList
import com.ltan.music.business.bean.Privileges

/**
 * TMusic.com.ltan.music.mine.beans
 *
 * @ClassName: PlayListDetail
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
data class PlayListDetailRsp(
    val code: Int = 0,
    val playlist: PlayList? = null,
    val privileges: List<Privileges>? = null
)