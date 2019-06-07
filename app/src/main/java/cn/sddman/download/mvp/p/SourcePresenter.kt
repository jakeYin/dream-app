package cn.sddman.download.mvp.p

import cn.sddman.download.mvp.e.MagnetRule

interface SourcePresenter {
    fun searchMagnet(rule: MagnetRule, keyword: String, page: Int)
}
