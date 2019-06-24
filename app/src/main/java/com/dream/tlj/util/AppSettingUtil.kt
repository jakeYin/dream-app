package com.dream.tlj.util

import com.dream.tlj.common.Const
import com.dream.tlj.mvp.m.AppSettingModel
import com.dream.tlj.mvp.m.AppSettingModelImp

class AppSettingUtil {
    private val model: AppSettingModel
    val fileSavePath: String
        get() {
            val savePath: String
            val setting = model.savePath
            if (null != setting) {
                savePath = setting.value!!
            } else {
                savePath = Const.File_SAVE_PATH
            }
            FileTools.mkdirs(savePath)
            return savePath
        }
    val fileCachePath: String
        get() {
            val savePath = Const.File_CACHE_PATH
            FileTools.mkdirs(savePath)
            return savePath
        }
    val downCount: Int
        get() {
            var count = 0
            val setting = model.downCount
            if (null != setting) {
                count = Integer.valueOf(setting.value)
            } else {
                count = Const.MAX_DOWN_COUNT
            }
            return count
        }

    val isMobileNetDown: Boolean?
        get() {
            val setting = model.mobileNet ?: return true
            return if (setting.value == Const.MOBILE_NET_OK.toString() + "") true else false
        }
    val isShowDownNotify: Boolean?
        get() {
            val setting = model.downNotify ?: return false
            return if (setting.value == Const.MOBILE_NET_OK.toString() + "") true else false
        }
    val isDown: Boolean?
        get() {
            val netType = SystemConfig.netType
            if (netType == Const.NET_TYPE_UNKNOW) {
                return false
            } else if (netType == Const.NET_TYPE_WIFI) {
                return true
            } else if (netType == Const.NET_TYPE_MOBILE && isMobileNetDown!!) {
                return true
            } else if (netType == Const.NET_TYPE_MOBILE && (!isMobileNetDown!!)) {
                return false
            }
            return false
        }

    init {
        model = AppSettingModelImp()
    }

    companion object {
        private var appSettingUtil: AppSettingUtil? = null
        val instance: AppSettingUtil
            @Synchronized get() {
                if (appSettingUtil == null) {
                    appSettingUtil = AppSettingUtil()
                }
                return appSettingUtil!!
            }
    }
}
