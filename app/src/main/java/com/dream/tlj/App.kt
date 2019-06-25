package com.dream.tlj

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import com.dream.tlj.common.DelegateApplicationPackageManager
import com.dream.tlj.util.AdUtil
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.umeng.commonsdk.UMConfigure
import com.xunlei.downloadlib.XLTaskHelper
import org.xutils.x


class App : Application() {
    companion object {
        var instance: App? = null
    }
    override fun onCreate() {
        super.onCreate()
        x.Ext.init(this)
        //x.Ext.setDebug(BuildConfig.DEBUG);
        XLTaskHelper.init(applicationContext)
        instance = this
        AdUtil.init(this)
        AdUtil.loadRewardAd()
        UMConfigure.init(this, "5d122dab570df312d20011c5", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,null);
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return true
            }
        })
    }

    override fun getPackageName(): String {
        return if (Log.getStackTraceString(Throwable()).contains("com.xunlei.downloadlib")) {
            "com.xunlei.downloadprovider"
        } else super.getPackageName()
    }

    override fun getPackageManager(): PackageManager {
        return if (Log.getStackTraceString(Throwable()).contains("com.xunlei.downloadlib")) {
            DelegateApplicationPackageManager(super.getPackageManager())
        } else super.getPackageManager()
    }


}
