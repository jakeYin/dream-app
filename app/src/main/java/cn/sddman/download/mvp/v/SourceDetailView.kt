package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.MagnetDetail
import cn.sddman.download.mvp.e.MagnetInfo

interface SourceDetailView {
    fun refreshData(magnetDetail: MagnetDetail?)
    fun clickItem(url: String)

}