package com.ltan.music.basemvp

import android.annotation.SuppressLint
import com.ltan.music.common.MusicLog
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.io.FileNotFoundException

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

    fun <T> async(work: Work<T>, main: Main<T>): Disposable {
        return Observable.create(ObservableOnSubscribe { e: ObservableEmitter<T> ->
            val data: T? = work.get()
            if (data == null) {
                e.onComplete()
                return@ObservableOnSubscribe
            }
            e.onNext(data)
            e.onComplete()
        } as ObservableOnSubscribe<T>)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t -> main.doOnMain(t) }, { e-> MusicLog.e("" + e.message)})
    }

    interface Work<T> {
        fun get(): T
    }

    interface Main<T> {
        @Throws(FileNotFoundException::class)
        fun doOnMain(t: T)
    }
}