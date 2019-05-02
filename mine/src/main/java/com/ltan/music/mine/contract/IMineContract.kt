package com.ltan.music.mine.contract

import com.ltan.music.basemvp.IBaseContract

/**
 * TMusic.com.ltan.music.index.contract
 *
 * @ClassName: IMineContract
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-30
 * @Version: 1.0
 */
interface IMineContract {

    interface View : IBaseContract.View {
        fun testView(p: Presenter)
    }

    interface Presenter : IBaseContract.Presenter {
        fun queryData()
    }
}