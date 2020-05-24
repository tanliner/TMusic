package com.ltan.music.contract

import com.ltan.music.basemvp.IBasePresenter
import com.ltan.music.basemvp.IBaseView

/**
 * TMusic.com.ltan.music.contract
 *
 * @ClassName: LoginContract
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
interface LoginContract {
    interface View : IBaseView {
        fun onLogoutSuccess()
        fun onLoginStatus(code: Int)
    }

    interface Presenter : IBasePresenter<View> {
        fun login(name: String, pass: String)
        /** Note: do http request */
        fun logout()
        fun query()
    }
}