package com.dream.tlj.mvp.v

import com.dream.tlj.mvp.e.TorrentInfoEntity

interface TorrentInfoView {
    val isDown: Boolean
    fun initTaskListView(list: List<TorrentInfoEntity>)
    fun itemClick(index: Int)
    fun startTaskSuccess()
    fun startTaskFail(msg: String)
    fun playVideo(te: TorrentInfoEntity)
}
