package com.ltan.music.mine

import com.ltan.music.widget.constants.State

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongListCategoryObject
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-03
 * @Version: 1.0
 */
data class SongListCategoryObject(
    val id: Int,
    val title: String = "",
    var count: Int = 0,
    var creatable: Boolean = false,
    var state: State = State.COLLAPSE
)