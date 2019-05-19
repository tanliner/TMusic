package com.ltan.music.mine

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ltan.music.basemvp.BaseMVPActivity
import com.ltan.music.common.MusicLog
import com.ltan.music.common.ToastUtil
import com.ltan.music.mine.adapter.SongItemBinder
import com.ltan.music.mine.beans.PlayListDetailRsp
import com.ltan.music.mine.beans.Tracks
import com.ltan.music.mine.contract.ISongListContract
import com.ltan.music.mine.presenter.SongListPresenter
import com.ltan.music.widget.ClickType
import com.ltan.music.widget.ListItemClickListener
import kotterknife.bindView
import me.drakeet.multitype.MultiTypeAdapter

/**
 * TMusic.com.ltan.music.mine
 *
 * @ClassName: SongListActivity
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-19
 * @Version: 1.0
 */
class SongListActivity : BaseMVPActivity<SongListPresenter>(), ISongListContract.View {

    companion object {
        const val ARG_SONG_ID = "songListId"
    }
    override fun initLayout(): Int {
        return R.layout.mine_activity_song_list
    }

    override fun initPresenter() {
        mPresenter.attachView(this)
    }

    private var songListId: Long = 0L
    private val songsRecycleView: RecyclerView by bindView(R.id.rcy_mine_song_list)
    private val backBtn: Button by bindView(R.id.btn_mine_song_list_back)
    private val rcyAdapter = MultiTypeAdapter()

    private lateinit var items: MutableList<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processArgs()
        initView()
        querySongList()
    }

    private fun processArgs() {
        songListId = intent.getLongExtra(ARG_SONG_ID, 0L)
        items = ArrayList()
    }

    private fun initView() {
        backBtn.setOnClickListener {
            MusicLog.d(TAG, "back button clicked")
            finish()
        }

        val songItemBinder = SongItemBinder()
        rcyAdapter.register(SongItemObject::class.java, songItemBinder)
        rcyAdapter.items = items

        songItemBinder.setOnItemClickListener(SongItemClick())
        songsRecycleView.layoutManager = LinearLayoutManager(this)
        songsRecycleView.adapter = rcyAdapter
    }

    private fun querySongList() {
        mPresenter.getPlayListDetail(songListId)
    }

    override fun onPlayListDetail(data: PlayListDetailRsp?) {
        if(data?.playlist == null) {
            ToastUtil.showToastShort(getString(R.string.mine_play_list_failed))
            return
        }
        MusicLog.d(TAG, "view onPlayListDetail: ${data.playlist}")
        val tracks = data.playlist.tracks
        if(tracks != null) {
            for (i in 0 until tracks.size) {
                val item = SongItemObject()
                // val item = SongItemObject()
                item.number = (i + 1)
                item.title = tracks[i].name
                item.subTitle = buildSubtitle(tracks[i])
                items.add(item)
            }
        }
        rcyAdapter.notifyDataSetChanged()
    }

    private fun buildSubtitle(tracks: Tracks): String {
        val sb = StringBuilder()
        val ar = tracks.ar
        if(ar != null) {
            for (n in ar) {
                sb.append(n.name).append('/')
            }
        }
        if(tracks.al != null) {
            sb.append(" - ").append(tracks.al.name)
        }
        return sb.toString()
    }

    class SongItemClick : ListItemClickListener {
        val TAG = "SongItemClick"

        override fun onItemClick(position: Int, v: View, type: ClickType) {
            MusicLog.d(TAG, "item click $position $v $type")
        }
    }
}