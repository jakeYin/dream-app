package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.MagnetDetail

interface SourceDetailView {
    fun refreshData(list: List<MagnetDetail>)
}
