package com.dream.tlj.mvp.p

import com.dream.tlj.mvp.e.MagnetRule

interface SourceDetailPresenter {
    fun parser(magnetRule: com.dream.tlj.mvp.e.MagnetRule, url:String)
}
