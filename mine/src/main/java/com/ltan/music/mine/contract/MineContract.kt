package com.ltan.music.mine.contract

import com.ltan.music.basemvp.IBaseContract
import com.ltan.music.business.bean.PlayList
import com.ltan.music.mine.beans.SongSubCunt

/**
 * TMusic.com.ltan.music.index.contract
 *
 * @ClassName: MineContract
 * @Description:
 * @Author: tanlin
 * @Date:   2019-04-30
 * @Version: 1.0
 */
interface MineContract {

    interface View : IBaseContract.View<Presenter> {
        fun onSubcount(data: SongSubCunt?)
        fun onPlayList(data: List<PlayList>?)
    }

    interface Presenter : IBaseContract.Presenter<View> {
        fun subcount()
        fun getPlayList(uid: Long)
        fun getFmRadio(uid: Long)
    }
}