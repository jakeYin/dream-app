package cn.sddman.download.util

import android.content.pm.PackageManager

import org.xutils.x

import cn.sddman.download.mvp.e.MagnetRule

class AppConfigUtil {
    var rules: List<MagnetRule>? = null
        get() {
            if (null == field) {
                GsonUtil.getRule(x.app().applicationContext, "search.json")
            }
            return field
        }

    companion object {
        private var appConfigUtil: AppConfigUtil? = null

        val instance: AppConfigUtil
            @Synchronized get() {
                if (appConfigUtil == null) {
                    appConfigUtil = AppConfigUtil()
                }
                return appConfigUtil!!
            }

        val localVersion: Int
            get() {
                var localVersion = 0
                try {
                    val packageInfo = x.app().applicationContext
                            .packageManager
                            .getPackageInfo(x.app().packageName, 0)
                    localVersion = packageInfo.versionCode
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

                return localVersion
            }

        val localVersionName: String
            get() {
                var localVersion = ""
                try {
                    val packageInfo = x.app().applicationContext
                            .packageManager
                            .getPackageInfo(x.app().packageName, 0)
                    localVersion = packageInfo.versionName
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

                return localVersion

            }
    }

}
