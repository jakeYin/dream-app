package cn.sddman.download.mvp.p

import cn.sddman.download.mvp.e.TorrentInfoEntity

interface TorrentInfoPresenter {
    fun startTask(checkList: List<TorrentInfoEntity>)
}
