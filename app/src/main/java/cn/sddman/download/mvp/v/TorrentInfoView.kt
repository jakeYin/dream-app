package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.TorrentInfoEntity

interface TorrentInfoView {
    val isDown: Boolean
    fun initTaskListView(list: List<TorrentInfoEntity>)
    fun itemClick(index: Int)
    fun startTaskSuccess()
    fun startTaskFail(msg: String)
    fun playerViedo(te: TorrentInfoEntity)
}
