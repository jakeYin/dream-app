package com.dream.tlj.mvp.p

interface AppSettingPresenter {
    fun initSetting()
    fun setSavePath(path: String)
    fun setDownCount(count: String)
    fun setMobileNet(net: String)
    fun setDownNotify(notify: String)
}
