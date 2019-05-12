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
    interface View : IBaseContract.View {
        fun testView(p: Presenter)
    }

    interface Presenter : IBaseContract.Presenter {
        fun login(name: String, pass: String)
    }
}