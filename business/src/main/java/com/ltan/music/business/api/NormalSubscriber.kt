package com.ltan.music.business.api

import com.google.gson.JsonParseException
import com.ltan.music.business.api.error.ResponseException
import com.ltan.music.common.MusicLog
import io.reactivex.subscribers.DisposableSubscriber

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: NormalSubscriber
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
abstract class NormalSubscriber<T> : DisposableSubscriber<T>() {
    val STATUS_TOKEN_INVALID = 1001401002 //token失效
    val JSON_PARSE_EXCEPTION = -1000 //json解析异常
    val UNKOWN = -1001


    override fun onComplete() {}

    override fun onError(e: Throwable) {
        // MusicLog.e("NormalSubscriber", "onError: $e")
        MusicLog.e("NormalSubscriber", "onError: ${e.printStackTrace()}")
        if (e is ResponseException) {
            e.message?.let { onError(e.code, it) }
        } else if (e is JsonParseException) {
            e.message?.let { onError(JSON_PARSE_EXCEPTION, it) }
        } else {
            onError(UNKOWN, "未知错误...")
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    open fun onError(errorCode: Int, errorMsg: String) {}
}