package com.ltan.music.basemvp

import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * TMusic.com.ltan.music.basemvp
 *
 * Simplify the thread switch， IO-thread/UI-thread
 *
 * @ClassName: RxUtils
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-18
 * @Version: 1.0
 */
object RxUtils {

    @JvmStatic
    fun <T> rxFlowableHelper(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}