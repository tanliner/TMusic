package com.ltan.music.presenter

import com.ltan.music.UserApi
import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.RxPresenter
import com.ltan.music.common.MusicLog
import com.ltan.music.contract.LoginContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * TMusic.com.ltan.music.presenter
 *
 * @ClassName: LoginPresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class LoginPresenter : RxPresenter<LoginContract.View>(), LoginContract.Presenter {
    override fun login(name: String, pass: String) {
        ApiProxy.instance.getApi(UserApi::class.java)
            .login(name, pass, true)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .subscribe(Consumer {
                MusicLog.d("LoginPresenter", "aaaa1   $it")
            }, Consumer {
                MusicLog.d("LoginPresenter", "aaaa2   $it")
            })

    }

}