package com.ltan.music.contract

import com.ltan.music.basemvp.IBaseContract

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
    interface View : IBaseContract.View<Presenter> {
        fun onLoginSuccess()
        fun onLogoutSuccess()
    }

    interface Presenter : IBaseContract.Presenter<View> {
        fun login(name: String, pass: String)
    }
}