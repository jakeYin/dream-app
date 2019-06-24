package com.dream.tlj.mvp.p

import com.dream.tlj.mvp.e.TorrentInfoEntity

interface TorrentInfoPresenter {
    fun startTask(checkList: List<TorrentInfoEntity>)
}
