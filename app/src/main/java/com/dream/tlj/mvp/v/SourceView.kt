package com.dream.tlj.mvp.v

import com.dream.tlj.mvp.e.MagnetInfo

interface SourceView {
    fun refreshData(info: List<com.dream.tlj.mvp.e.MagnetInfo>?)
    fun clickItem(magnet: com.dream.tlj.mvp.e.MagnetInfo)

}
