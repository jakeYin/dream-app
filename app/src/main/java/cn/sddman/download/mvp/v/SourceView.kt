package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.MagnetInfo

interface SourceView {
    fun refreshData(info: List<MagnetInfo>?)
    fun clickItem(magnet: MagnetInfo)

}
