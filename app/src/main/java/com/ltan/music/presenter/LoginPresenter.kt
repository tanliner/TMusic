package com.ltan.music.presenter

import com.ltan.music.UserApi
import com.ltan.music.account.beans.LoginResult
import com.ltan.music.account.utils.AccountUtil
import com.ltan.music.business.api.ApiProxy
import com.ltan.music.business.api.NormalSubscriber
import com.ltan.music.business.api.RxPresenter
import com.ltan.music.common.MusicLog
import com.ltan.music.contract.LoginContract

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
        observe(ApiProxy.instance.getApi(UserApi::class.java)
            .login(name, pass))
            .safeSubscribe(object : NormalSubscriber<LoginResult>() {
                override fun onNext(t: LoginResult) {

                    MusicLog.d(LoginPresenter.TAG, "login onNext   $t")
                    AccountUtil.saveAccountInfo(t.account)
                    AccountUtil.saveProfileInfo(t.profile)
                    mView.onLoginStatus(t.code)
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    MusicLog.d(LoginPresenter.TAG, "login onError error code $errorCode")
                }
            })
    }

    override fun logout() {
        observe(ApiProxy.instance.getApi(UserApi::class.java)
            .logout())
            .safeSubscribe(object : NormalSubscriber<Any?>() {
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
        observe(ApiProxy.instance.getApi(UserApi::class.java)
            .query())
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