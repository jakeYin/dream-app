package cn.sddman.download.mvp.m

import org.xutils.ex.DbException

import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.AppSettingEntity
import cn.sddman.download.util.DBTools

class AppSettingModelImp : AppSettingModel {

    override val savePath: AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(AppSettingEntity::class.java).where("key", "=", Const.SAVE_PATH_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }

    override val downCount: AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(AppSettingEntity::class.java).where("key", "=", Const.DOWN_COUNT_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }

    override val mobileNet: AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(AppSettingEntity::class.java).where("key", "=", Const.MOBILE_NET_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }
    override val downNotify: AppSettingEntity?
        get() {
            try {
                return DBTools.instance.db().selector(AppSettingEntity::class.java).where("key", "=", Const.DOWN_NOTIFY_KEY).findFirst()
            } catch (e: DbException) {
                e.printStackTrace()
            }

            return null
        }

    override fun findAllSetting(): List<AppSettingEntity>? {
        try {
            return DBTools.instance.db().findAll(AppSettingEntity::class.java)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return null
    }

    override fun saveOrUploadSteeing(setting: AppSettingEntity) {
        try {
            DBTools.instance.db().saveOrUpdate(setting)
        } catch (e: DbException) {
            e.printStackTrace()
        }

    }

    override fun setSavePath(path: String) {
        var setting = savePath
        if (null == setting) {
            setting = AppSettingEntity()
            setting.key = Const.SAVE_PATH_KEY
        }
        setting.value = path
        saveOrUploadSteeing(setting)
    }

    override fun setDownCount(count: String) {
        var setting = downCount
        if (null == setting) {
            setting = AppSettingEntity()
            setting.key = Const.DOWN_COUNT_KEY
        }
        setting.value = count
        saveOrUploadSteeing(setting)
    }

    override fun setMobileNet(net: String) {
        var setting = mobileNet
        if (null == setting) {
            setting = AppSettingEntity()
            setting.key = Const.MOBILE_NET_KEY
        }
        setting.value = net
        saveOrUploadSteeing(setting)
    }

    override fun setDownNotify(notify: String) {
        var setting = downNotify
        if (null == setting) {
            setting = AppSettingEntity()
            setting.key = Const.DOWN_NOTIFY_KEY
        }
        setting.value = notify
        saveOrUploadSteeing(setting)
    }
}
