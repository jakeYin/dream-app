package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.MagnetInfo

interface MagnetSearchView {
    fun refreshData(info: List<MagnetInfo>)
    fun moreOption(magnet: MagnetInfo)
}
