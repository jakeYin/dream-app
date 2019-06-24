package com.dream.tlj.mvp.v

import com.dream.tlj.mvp.e.MagnetInfo

interface MagnetSearchView {
    fun refreshData(info: List<com.dream.tlj.mvp.e.MagnetInfo>)
    fun moreOption(magnet: com.dream.tlj.mvp.e.MagnetInfo)
}
