package com.dream.tlj.mvp.p

import com.dream.tlj.mvp.m.AppSettingModel
import com.dream.tlj.mvp.m.AppSettingModelImp
import com.dream.tlj.mvp.v.AppSettingView

class AppSettingPresenterImp(private val appSettingView: AppSettingView) : AppSettingPresenter {
    private val appSettingModel: AppSettingModel

    init {
        appSettingModel = AppSettingModelImp()
        initSetting()
    }

    override fun initSetting() {
        val list = appSettingModel.findAllSetting()
        if (null != list && list.size > 0) {
            for (setting in list) {
                appSettingView.initSetting(setting.key!!, setting.value!!)
            }
        }
    }

    override fun setSavePath(path: String) {
        appSettingModel.setSavePath(path)
    }

    override fun setDownCount(count: String) {
        appSettingModel.setDownCount(count)
    }

    override fun setMobileNet(net: String) {
        appSettingModel.setMobileNet(net)
    }

    override fun setDownNotify(notify: String) {
        appSettingModel.setDownNotify(notify)
    }
}
