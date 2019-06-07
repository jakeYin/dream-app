package cn.sddman.download.mvp.m

import cn.sddman.download.mvp.e.AppSettingEntity

interface AppSettingModel {
    val savePath: AppSettingEntity?
    val downCount: AppSettingEntity?
    val mobileNet: AppSettingEntity?
    val downNotify: AppSettingEntity?
    fun findAllSetting(): List<AppSettingEntity>?
    fun saveOrUploadSteeing(setting: AppSettingEntity)
    fun setSavePath(path: String)
    fun setDownCount(count: String)
    fun setMobileNet(net: String)
    fun setDownNotify(notify: String)

}
