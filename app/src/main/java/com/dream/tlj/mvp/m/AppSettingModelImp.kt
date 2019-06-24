package com.dream.tlj.mvp.m

import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.AppSettingEntity
import com.dream.tlj.util.DBTools
import org.xutils.ex.DbException

class AppSettingModelImp : AppSettingModel {

    override val savePath: com.dream.tlj.mvp.e.AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(com.dream.tlj.mvp.e.AppSettingEntity::class.java).where("key", "=", Const.SAVE_PATH_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }

    override val downCount: com.dream.tlj.mvp.e.AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(com.dream.tlj.mvp.e.AppSettingEntity::class.java).where("key", "=", Const.DOWN_COUNT_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }

    override val mobileNet: com.dream.tlj.mvp.e.AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(com.dream.tlj.mvp.e.AppSettingEntity::class.java).where("key", "=", Const.MOBILE_NET_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }
    override val downNotify: com.dream.tlj.mvp.e.AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(com.dream.tlj.mvp.e.AppSettingEntity::class.java).where("key", "=", Const.DOWN_NOTIFY_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }

    override fun findAllSetting(): List<com.dream.tlj.mvp.e.AppSettingEntity>? {
        try {
            return DBTools.instance.db().findAll(com.dream.tlj.mvp.e.AppSettingEntity::class.java)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun saveOrUploadSteeing(setting: com.dream.tlj.mvp.e.AppSettingEntity) {
        try {
            DBTools.instance.db().saveOrUpdate(setting)
        } catch (e: DbException) {
            e.printStackTrace()
        }

    }

    override fun setSavePath(path: String) {
        var setting = savePath
        if (null == setting) {
            setting = com.dream.tlj.mvp.e.AppSettingEntity()
            setting.key = Const.SAVE_PATH_KEY
        }
        setting.value = path
        saveOrUploadSteeing(setting)
    }

    override fun setDownCount(count: String) {
        var setting = downCount
        if (null == setting) {
            setting = com.dream.tlj.mvp.e.AppSettingEntity()
            setting.key = Const.DOWN_COUNT_KEY
        }
        setting.value = count
        saveOrUploadSteeing(setting)
    }

    override fun setMobileNet(net: String) {
        var setting = mobileNet
        if (null == setting) {
            setting = com.dream.tlj.mvp.e.AppSettingEntity()
            setting.key = Const.MOBILE_NET_KEY
        }
        setting.value = net
        saveOrUploadSteeing(setting)
    }

    override fun setDownNotify(notify: String) {
        var setting = downNotify
        if (null == setting) {
            setting = com.dream.tlj.mvp.e.AppSettingEntity()
            setting.key = Const.DOWN_NOTIFY_KEY
        }
        setting.value = notify
        saveOrUploadSteeing(setting)
    }
}
