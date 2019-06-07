package cn.sddman.download.listener

import cn.sddman.download.mvp.e.MagnetInfo

interface MagnetSearchListener {
    fun success(info: List<MagnetInfo>)
    fun fail(error: String)
}
