package com.dream.tlj.mvp.p

import com.dream.tlj.mvp.e.MagnetRule

interface SourcePresenter {
    fun searchMagnet(rule: com.dream.tlj.mvp.e.MagnetRule, keyword: String, page: Int)
}
