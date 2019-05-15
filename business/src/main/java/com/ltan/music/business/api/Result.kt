package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: Result
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
data class Result<T>(val popAdjust: Boolean, val data: T)