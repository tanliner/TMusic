package com.ltan.music.service.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ltan.music.common.bean.SongItemObject
import com.ltan.music.service.PlayerCDFragment

/**
 * TMusic.com.ltan.music.service.adapter
 *
 * @ClassName: PlayerPageAdapter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-06-23
 * @Version: 1.0
 */
class PlayerPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    companion object {
        private const val TAG = "PlayerPageAdapter"
    }

    var mClick: CdClickListener? = null

    private var mSongItems = ArrayList<SongItemObject>()

    fun setSongs(songs: ArrayList<SongItemObject>) {
        mSongItems.clear()
        mSongItems.addAll(songs)
        notifyDataSetChanged()
    }

    fun setOnClickListener(l: CdClickListener) {
        mClick = l
    }

    override fun getItem(position: Int): Fragment {
        val frgPlayerCD = PlayerCDFragment.newInstance()
        val args = Bundle()
        args.putParcelable(PlayerCDFragment.ARGS_SONG, mSongItems[position])
        frgPlayerCD.arguments = args
        frgPlayerCD.setCdClickListener(mClick)
        return frgPlayerCD
    }

    override fun getCount(): Int {
        return mSongItems.size
    }
}