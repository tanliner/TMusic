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

    override fun login(name: String, pass: String) {
        observe(ApiProxy.instance.getApi(UserApi::class.java)
            .login(name, pass))
            .safeSubscribe(object : NormalSubscriber<LoginResult>() {
                override fun onNext(t: LoginResult) {

                    MusicLog.d("login onNext   $t")
                    AccountUtil.saveAccountInfo(t.account)
                    AccountUtil.saveProfileInfo(t.profile)
                    mView.onLoginStatus(t.code)
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    MusicLog.d("login onError error code $errorCode")
                }
            })
    }

    override fun logout() {
        // clear first
        AccountUtil.saveAccountInfo(null)
        AccountUtil.saveProfileInfo(null)
        observe(ApiProxy.instance.getApi(UserApi::class.java)
            .logout())
            .safeSubscribe(object : NormalSubscriber<Any?>() {
                override fun onNext(rsp: Any?) {
                    MusicLog.d("logout, onNext $rsp")
                    mView.onLogoutSuccess()
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    MusicLog.d("logout, error code $errorCode, msg: $errorMsg")
                }
            })
    }

    override fun query() {
        observe(ApiProxy.instance.getApi(UserApi::class.java)
            .query())
            .safeSubscribe(object : NormalSubscriber<String>() {
                override fun onNext(t: String) {
                    MusicLog.d("query onNext any\n$t ")
                }

                override fun onComplete() {
                    MusicLog.d("query onComplete")
                }
            })
    }

}