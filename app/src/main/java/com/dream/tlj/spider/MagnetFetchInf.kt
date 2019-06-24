package com.dream.tlj.spider

import com.dream.tlj.mvp.e.MagnetDetail
import com.dream.tlj.mvp.e.MagnetInfo
import com.dream.tlj.mvp.e.MagnetRule

abstract class MagnetFetchInf {
    open fun transformPage(page: Int?): Int {
        return if (page == null || page <= 0) 1 else page
    }
    open fun parser(rule: com.dream.tlj.mvp.e.MagnetRule, keyword: String, page: Int): List<com.dream.tlj.mvp.e.MagnetInfo> {
        return arrayListOf();
    }
    open fun parser(rule: com.dream.tlj.mvp.e.MagnetRule, url:String): List<com.dream.tlj.mvp.e.MagnetDetail> {
        return arrayListOf();
    }
}