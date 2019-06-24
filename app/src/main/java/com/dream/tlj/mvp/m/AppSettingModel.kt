package com.dream.tlj.mvp.m

import com.dream.tlj.mvp.e.AppSettingEntity

interface AppSettingModel {
    val savePath: com.dream.tlj.mvp.e.AppSettingEntity?
    val downCount: com.dream.tlj.mvp.e.AppSettingEntity?
    val mobileNet: com.dream.tlj.mvp.e.AppSettingEntity?
    val downNotify: com.dream.tlj.mvp.e.AppSettingEntity?
    fun findAllSetting(): List<com.dream.tlj.mvp.e.AppSettingEntity>?
    fun saveOrUploadSteeing(setting: com.dream.tlj.mvp.e.AppSettingEntity)
    fun setSavePath(path: String)
    fun setDownCount(count: String)
    fun setMobileNet(net: String)
    fun setDownNotify(notify: String)

}
