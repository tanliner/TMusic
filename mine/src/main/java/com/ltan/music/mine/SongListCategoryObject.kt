package com.ltan.music.mine

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongListCategoryObject
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
class SongListCategoryObject constructor(val title: String = "", val count: Int = 0, var creatable: Boolean = false) {
    enum class STATE {
        EXPAND,
        SHRINK
    }

    var state = STATE.EXPAND
}