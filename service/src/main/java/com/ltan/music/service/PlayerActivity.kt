package com.ltan.music.service

import android.os.Bundle
import com.jaeger.library.StatusBarUtil
import com.ltan.music.basemvp.MusicBaseActivity
import com.ltan.music.common.bean.SongItemObject

/**
 * TMusic.com.ltan.music.service
 * playing fragment container
 *
 * @ClassName: PlayerActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-17
 * @Version: 1.0
 */
class PlayerActivity : MusicBaseActivity() {
    companion object {
        const val ARG_OBJ = "song"
        const val ARG_SONG_LIST = "song_list"
    }

    override fun initLayout(): Int {
        return R.layout.service_player_activity
    }

    private lateinit var mCurSong: SongPlaying
    private lateinit var mSongList: ArrayList<SongItemObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        super.onCreate(savedInstanceState)
        processArg()

        val transaction = supportFragmentManager.beginTransaction()
        val target = PlayFragment.newInstance()
        val args = Bundle()
        args.putParcelable(ARG_OBJ, mCurSong)
        args.putParcelableArrayList(ARG_SONG_LIST, mSongList)
        target.arguments = args
        transaction.add(R.id.container, target)
        transaction.commit()
    }

    private fun processArg() {
        mCurSong = intent.getParcelableExtra(ARG_OBJ)
        mSongList = intent.getParcelableArrayListExtra<SongItemObject>(ARG_SONG_LIST)

    }
}