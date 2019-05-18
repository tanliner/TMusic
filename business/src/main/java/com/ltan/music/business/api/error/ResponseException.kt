package com.ltan.music.business.api.error

/**
 * TMusic.com.ltan.music.business.api.error
 *
 * @ClassName: ResponseException
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class ResponseException(msg: String, val code: Int) : Exception(msg)