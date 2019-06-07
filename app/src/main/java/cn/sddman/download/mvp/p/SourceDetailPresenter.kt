package cn.sddman.download.mvp.p

import cn.sddman.download.mvp.e.MagnetRule

interface SourceDetailPresenter {
    fun parser(magnetRule: MagnetRule,url:String)
}
