package com.ltan.music.presenter

import com.ltan.music.UserApi
import com.ltan.music.account.beans.LoginResult
import com.ltan.music.account.utils.AccountUtil
import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.business.api.RxPresenter
import com.ltan.music.common.MusicLog
import com.ltan.music.contract.LoginContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber

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

    companion object {
        const val TAG = "Presenter/login"
    }

    override fun login(name: String, pass: String) {
        ApiProxy.instance.getApi(UserApi::class.java)
            .login(name, pass)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .subscribe(object : NormalSubscriber<LoginResult>(), Subscriber<LoginResult> {
                override fun onNext(t: LoginResult) {
                    MusicLog.d(LoginPresenter.TAG, "login onNext   $t")
                    AccountUtil.saveAccountInfo(t.account)
                    AccountUtil.saveProfileInfo(t.profile)
                    mView.onLoginSuccess()
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    MusicLog.d(LoginPresenter.TAG, "login onError error code $errorCode")
                }
            })
    }

    override fun logout() {
        ApiProxy.instance.getApi(UserApi::class.java)
            .logout()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .subscribe(object : NormalSubscriber<Any?>(), Subscriber<Any?> {
                override fun onNext(rsp: Any?) {
                    MusicLog.d(LoginPresenter.TAG, "logout, onNext $rsp")
                    AccountUtil.saveAccountInfo(null)
                    AccountUtil.saveProfileInfo(null)
                    mView.onLogoutSuccess()
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    MusicLog.d(LoginPresenter.TAG, "logout, error code $errorCode, msg: $errorMsg")
                    AccountUtil.saveAccountInfo(null)
                    AccountUtil.saveProfileInfo(null)
                }
            })
    }

    override fun query() {
        ApiProxy.instance.getApi(UserApi::class.java)
            .query()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .safeSubscribe(object : NormalSubscriber<String>() {
                override fun onNext(t: String) {
                    MusicLog.d(LoginPresenter.TAG, "query onNext any\n$t ")
                }

                override fun onComplete() {
                    MusicLog.d(LoginPresenter.TAG, "query onComplete")
                }
            })
    }

}