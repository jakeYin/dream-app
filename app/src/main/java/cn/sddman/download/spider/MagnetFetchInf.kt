package cn.sddman.bt.spider

import cn.sddman.download.mvp.e.MagnetDetail
import cn.sddman.download.mvp.e.MagnetInfo
import cn.sddman.download.mvp.e.MagnetRule
import cn.sddman.download.mvp.e.MagnetSearchBean

abstract class MagnetFetchInf {
    open fun transformPage(page: Int?): Int {
        return if (page == null || page <= 0) 1 else page
    }
    open fun parser(rule: MagnetRule, keyword: String, page: Int): List<MagnetInfo>? {
        return null;
    }
    open fun parser(rule: MagnetRule, url:String): MagnetDetail? {
        return null;
    }
}