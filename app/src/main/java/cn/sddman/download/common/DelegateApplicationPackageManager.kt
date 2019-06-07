package cn.sddman.download.common

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.ChangedPackages
import android.content.pm.FeatureInfo
import android.content.pm.InstrumentationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.content.pm.ProviderInfo
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.content.pm.SharedLibraryInfo
import android.content.pm.VersionedPackage
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.UserHandle
import android.support.annotation.RequiresApi
import android.util.Log

/**
 * Created by oceanzhang on 2016/10/28.
 */

class DelegateApplicationPackageManager(internal var packageManager: PackageManager) : PackageManager() {

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getPackageInfo(packageName: String, flags: Int): PackageInfo {
        Log.w(TAG, "getPackageInfo() :$packageName")
        val pi = packageManager.getPackageInfo(realPackageName, flags)
        pi.applicationInfo.packageName = packageName
        pi.packageName = packageName
        return pi
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getPackageInfo(versionedPackage: VersionedPackage, i: Int): PackageInfo? {
        return null
    }

    override fun currentToCanonicalPackageNames(names: Array<String>): Array<String> {
        return packageManager.currentToCanonicalPackageNames(names)
    }

    override fun canonicalToCurrentPackageNames(names: Array<String>): Array<String> {
        return packageManager.canonicalToCurrentPackageNames(names)
    }

    override fun getLaunchIntentForPackage(packageName: String): Intent? {
        Log.w(TAG, "getLaunchIntentForPackage() ")
        return packageManager.getLaunchIntentForPackage(packageName)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun getLeanbackLaunchIntentForPackage(packageName: String): Intent? {
        Log.w(TAG, "getLeanbackLaunchIntentForPackage() ")
        return packageManager.getLeanbackLaunchIntentForPackage(packageName)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getPackageGids(packageName: String): IntArray {
        Log.w(TAG, "getPackageGids() ")
        return getPackageGids(packageName, 0)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getPackageGids(packageName: String, flags: Int): IntArray {
        Log.w(TAG, "getPackageGids() ")
        return getPackageGids(packageName, flags)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Throws(PackageManager.NameNotFoundException::class)
    override fun getPackageUid(packageName: String, flags: Int): Int {
        Log.w(TAG, "getPackageUid() ")
        return packageManager.getPackageUid(packageName, flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getPermissionInfo(name: String, flags: Int): PermissionInfo {
        Log.w(TAG, "getPermissionInfo() ")
        return packageManager.getPermissionInfo(name, flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun queryPermissionsByGroup(group: String, flags: Int): List<PermissionInfo> {
        return packageManager.queryPermissionsByGroup(group, flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getPermissionGroupInfo(name: String,
                                        flags: Int): PermissionGroupInfo {
        return packageManager.getPermissionGroupInfo(name, flags)
    }

    override fun getAllPermissionGroups(flags: Int): List<PermissionGroupInfo> {
        return packageManager.getAllPermissionGroups(flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo {
        var packageName = packageName
        Log.w(TAG, "getApplicationInfo() ")
        if ("com.xunlei.downloadprovider" == packageName) {
            packageName = realPackageName
        }
        var applicationInfo: ApplicationInfo
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, flags)
            Log.w("APPKEY", applicationInfo.metaData.getString("com.xunlei.download.APP_KEY"))
        } catch (e: Exception) {
            applicationInfo = ApplicationInfo()
        }

        var metaData: Bundle? = applicationInfo.metaData
        if (metaData == null) {
            metaData = Bundle()
            applicationInfo.metaData = metaData
        }
        metaData.putString("com.xunlei.download.APP_KEY", "bpIzNjAxNTsxNTA0MDk0ODg4LjQyODAwMAOxNw==^a2cec7^10e7f1756b15519e20ffb6cf0fbf671f")
        return applicationInfo
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getActivityInfo(className: ComponentName, flags: Int): ActivityInfo {
        Log.w(TAG, "getActivityInfo() " + className.className)
        return packageManager.getActivityInfo(className, flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getReceiverInfo(className: ComponentName, flags: Int): ActivityInfo {
        return packageManager.getReceiverInfo(className, flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getServiceInfo(className: ComponentName, flags: Int): ServiceInfo {
        return packageManager.getServiceInfo(className, flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getProviderInfo(className: ComponentName, flags: Int): ProviderInfo {
        return packageManager.getProviderInfo(className, flags)
    }

    override fun getSystemSharedLibraryNames(): Array<String> {
        return packageManager.systemSharedLibraryNames
    }

    override fun getSharedLibraries(i: Int): List<SharedLibraryInfo>? {
        return null
    }

    override fun getChangedPackages(i: Int): ChangedPackages? {
        return null
    }


    override fun getSystemAvailableFeatures(): Array<FeatureInfo> {
        return packageManager.systemAvailableFeatures
    }

    override fun hasSystemFeature(name: String): Boolean {
        return packageManager.hasSystemFeature(name)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun hasSystemFeature(name: String, version: Int): Boolean {
        return packageManager.hasSystemFeature(name, version)
    }

    override fun checkPermission(permName: String, pkgName: String): Int { //TODO packagename
        return packageManager.checkPermission(permName, pkgName)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun isPermissionRevokedByPolicy(permName: String, pkgName: String): Boolean {
        Log.w(TAG, "isPermissionRevokedByPolicy() ")
        return packageManager.isPermissionRevokedByPolicy(permName, pkgName)
    }

    override fun addPermission(info: PermissionInfo): Boolean {
        return packageManager.addPermission(info)
    }

    override fun addPermissionAsync(info: PermissionInfo): Boolean {
        return packageManager.addPermissionAsync(info)
    }

    override fun removePermission(name: String) {
        packageManager.removePermission(name)
    }

    override fun checkSignatures(pkg1: String, pkg2: String): Int {
        return packageManager.checkSignatures(pkg1, pkg2)
    }

    override fun checkSignatures(uid1: Int, uid2: Int): Int {
        return packageManager.checkSignatures(uid1, uid2)
    }

    override fun getPackagesForUid(uid: Int): Array<String>? {
        return packageManager.getPackagesForUid(uid)
    }

    override fun getNameForUid(uid: Int): String? {
        return packageManager.getNameForUid(uid)
    }

    override fun getInstalledPackages(flags: Int): List<PackageInfo> {
        return packageManager.getInstalledPackages(flags)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun getPackagesHoldingPermissions(
            permissions: Array<String>, flags: Int): List<PackageInfo> {
        return packageManager.getPackagesHoldingPermissions(permissions, flags)
    }

    override fun getInstalledApplications(flags: Int): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(flags)
    }

    override fun isInstantApp(): Boolean {
        return false
    }

    override fun isInstantApp(s: String): Boolean {
        return false
    }

    override fun getInstantAppCookieMaxBytes(): Int {
        return 0
    }

    override fun getInstantAppCookie(): ByteArray {
        return ByteArray(0)
    }

    override fun clearInstantAppCookie() {

    }

    override fun updateInstantAppCookie(bytes: ByteArray?) {

    }

    override fun resolveActivity(intent: Intent, flags: Int): ResolveInfo? {
        val componentName = intent.component
        Log.d(TAG, "resolveActivity" + componentName!!.className)
        intent.component = ComponentName(realPackageName, componentName.className)
        intent.setPackage(realPackageName)
        return packageManager.resolveActivity(intent, flags)
    }

    override fun queryIntentActivities(intent: Intent,
                                       flags: Int): List<ResolveInfo> {
        return packageManager.queryIntentActivities(intent, flags)
    }


    override fun queryIntentActivityOptions(
            caller: ComponentName?, specifics: Array<Intent>?, intent: Intent,
            flags: Int): List<ResolveInfo> {
        return packageManager.queryIntentActivityOptions(caller, specifics, intent, flags)
    }

    override fun queryBroadcastReceivers(intent: Intent, flags: Int): List<ResolveInfo> {
        return packageManager.queryBroadcastReceivers(intent, flags)
    }

    override fun resolveService(intent: Intent, flags: Int): ResolveInfo {
        val componentName = intent.component
        Log.d(TAG, "resolveService" + componentName!!.className)
        intent.component = ComponentName(realPackageName, componentName.className)
        intent.setPackage(realPackageName)
        return packageManager.resolveService(intent, flags)
    }

    override fun queryIntentServices(intent: Intent, flags: Int): List<ResolveInfo> {
        return packageManager.queryIntentServices(intent, flags)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun queryIntentContentProviders(intent: Intent, flags: Int): List<ResolveInfo> {
        return packageManager.queryIntentContentProviders(intent, flags)
    }

    override fun resolveContentProvider(name: String, flags: Int): ProviderInfo {
        return packageManager.resolveContentProvider(name, flags)
    }


    override fun queryContentProviders(processName: String,
                                       uid: Int, flags: Int): List<ProviderInfo> {
        return packageManager.queryContentProviders(processName, uid, flags)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getInstrumentationInfo(
            className: ComponentName, flags: Int): InstrumentationInfo {
        return packageManager.getInstrumentationInfo(className, flags)
    }

    override fun queryInstrumentation(
            targetPackage: String, flags: Int): List<InstrumentationInfo> {
        return packageManager.queryInstrumentation(targetPackage, flags)
    }

    override fun getDrawable(packageName: String, resId: Int,
                             appInfo: ApplicationInfo): Drawable {
        Log.w(TAG, "getDrawable() ")
        return packageManager.getDrawable(packageName, resId, appInfo)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getActivityIcon(activityName: ComponentName?): Drawable {
        return packageManager.getActivityIcon(activityName)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getActivityIcon(intent: Intent): Drawable {
        if (intent.component != null) {
            return getActivityIcon(intent.component)
        }

        val info = resolveActivity(
                intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (info != null) {
            return info.activityInfo.loadIcon(this)
        }

        throw PackageManager.NameNotFoundException(intent.toUri(0))
    }

    override fun getDefaultActivityIcon(): Drawable {
        return packageManager.defaultActivityIcon
    }

    override fun getApplicationIcon(info: ApplicationInfo): Drawable {
        return info.loadIcon(this)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getApplicationIcon(packageName: String): Drawable {
        Log.w(TAG, "getApplicationIcon() ")
        return packageManager.getApplicationIcon(packageName)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Throws(PackageManager.NameNotFoundException::class)
    override fun getActivityBanner(activityName: ComponentName): Drawable {
        return packageManager.getActivityBanner(activityName)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Throws(PackageManager.NameNotFoundException::class)
    override fun getActivityBanner(intent: Intent): Drawable {
        return packageManager.getActivityBanner(intent)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    override fun getApplicationBanner(info: ApplicationInfo): Drawable {
        return packageManager.getApplicationBanner(info)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Throws(PackageManager.NameNotFoundException::class)
    override fun getApplicationBanner(packageName: String): Drawable {
        Log.w(TAG, "getApplicationBanner() ")
        return packageManager.getApplicationBanner(packageName)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getActivityLogo(activityName: ComponentName?): Drawable {
        return packageManager.getActivityLogo(activityName)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getActivityLogo(intent: Intent): Drawable {
        if (intent.component != null) {
            return getActivityLogo(intent.component)
        }

        val info = resolveActivity(
                intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (info != null) {
            return info.activityInfo.loadLogo(this)
        }

        throw PackageManager.NameNotFoundException(intent.toUri(0))
    }

    override fun getApplicationLogo(info: ApplicationInfo): Drawable {
        return info.loadLogo(this)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getApplicationLogo(packageName: String): Drawable {
        Log.w(TAG, "getApplicationLogo() ")
        return packageManager.getApplicationLogo(packageName)
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun getUserBadgedIcon(icon: Drawable, user: UserHandle): Drawable {
        return packageManager.getUserBadgedIcon(icon, user)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun getUserBadgedDrawableForDensity(drawable: Drawable, user: UserHandle,
                                                 badgeLocation: Rect, badgeDensity: Int): Drawable {
        return packageManager.getUserBadgedDrawableForDensity(drawable, user, badgeLocation, badgeDensity)
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun getUserBadgedLabel(label: CharSequence, user: UserHandle): CharSequence {
        return packageManager.getUserBadgedLabel(label, user)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getResourcesForActivity(activityName: ComponentName): Resources {
        return packageManager.getResourcesForActivity(activityName)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getResourcesForApplication(app: ApplicationInfo): Resources {
        return packageManager.getResourcesForApplication(app)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getResourcesForApplication(appPackageName: String): Resources {
        Log.w(TAG, "getResourcesForApplication() ")
        return packageManager.getResourcesForApplication(appPackageName)
    }


    override fun isSafeMode(): Boolean {
        return packageManager.isSafeMode
    }

    override fun setApplicationCategoryHint(s: String, i: Int) {

    }


    override fun getText(packageName: String, resid: Int,
                         appInfo: ApplicationInfo): CharSequence {
        Log.w(TAG, "getText() ")
        return packageManager.getText(packageName, resid, appInfo)
    }

    override fun getXml(packageName: String, resid: Int,
                        appInfo: ApplicationInfo): XmlResourceParser {
        Log.w(TAG, "getXml() ")
        return packageManager.getXml(packageName, resid, appInfo)
    }

    override fun getApplicationLabel(info: ApplicationInfo): CharSequence {
        Log.w(TAG, "getApplicationLabel() ")
        return packageManager.getApplicationLabel(info)
    }


    override fun verifyPendingInstall(id: Int, response: Int) {
        Log.w(TAG, "verifyPendingInstall() ")
        packageManager.verifyPendingInstall(id, response)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun extendVerificationTimeout(id: Int, verificationCodeAtTimeout: Int,
                                           millisecondsToDelay: Long) {
        Log.w(TAG, "extendVerificationTimeout() ")
        packageManager.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay)
    }


    override fun setInstallerPackageName(targetPackage: String,
                                         installerPackageName: String) {
        Log.w(TAG, "setInstallerPackageName() ")
        packageManager.setInstallerPackageName(targetPackage, installerPackageName)
    }

    override fun getInstallerPackageName(packageName: String): String {
        Log.w(TAG, "getInstallerPackageName() ")
        return packageManager.getInstallerPackageName(packageName)
    }


    override fun addPackageToPreferred(packageName: String) {
        Log.w(TAG, "addPackageToPreferred() ")
        packageManager.addPackageToPreferred(packageName)
    }

    override fun removePackageFromPreferred(packageName: String) {
        Log.w(TAG, "removePackageFromPreferred() is a no-op")
        packageManager.removePackageFromPreferred(packageName)
    }

    override fun getPreferredPackages(flags: Int): List<PackageInfo> {
        Log.w(TAG, "getPreferredPackages() is a no-op")
        return packageManager.getPreferredPackages(flags)
    }

    override fun addPreferredActivity(filter: IntentFilter,
                                      match: Int, set: Array<ComponentName>, activity: ComponentName) {
        Log.w(TAG, "addPreferredActivity() ")
        packageManager.addPreferredActivity(filter, match, set, activity)
    }


    override fun clearPackagePreferredActivities(packageName: String) {
        Log.w(TAG, "clearPackagePreferredActivities() ")
        packageManager.clearPackagePreferredActivities(packageName)
    }

    override fun getPreferredActivities(outFilters: List<IntentFilter>,
                                        outActivities: List<ComponentName>, packageName: String): Int {
        Log.w(TAG, "getPreferredActivities() ")
        return packageManager.getPreferredActivities(outFilters, outActivities, packageName)
    }

    override fun setComponentEnabledSetting(componentName: ComponentName,
                                            newState: Int, flags: Int) {
        packageManager.setComponentEnabledSetting(componentName, newState, flags)
    }

    override fun getComponentEnabledSetting(componentName: ComponentName): Int {
        return packageManager.getComponentEnabledSetting(componentName)
    }

    override fun setApplicationEnabledSetting(packageName: String,
                                              newState: Int, flags: Int) {
        Log.w(TAG, "setApplicationEnabledSetting() ")
        packageManager.setApplicationEnabledSetting(packageName, newState, flags)
    }

    override fun getApplicationEnabledSetting(packageName: String): Int {
        Log.w(TAG, "getApplicationEnabledSetting() ")
        return packageManager.getApplicationEnabledSetting(packageName)
    }


    fun getApplicationHiddenSettingAsUser(packageName: String, user: UserHandle): Boolean {
        Log.w(TAG, "getApplicationHiddenSettingAsUser() ")
        return getApplicationHiddenSettingAsUser(packageName, user)
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun getPackageInstaller(): PackageInstaller {
        Log.w(TAG, "getPackageInstaller() ")
        return packageManager.packageInstaller
    }

    override fun canRequestPackageInstalls(): Boolean {
        return false
    }

    companion object {
        private val TAG = "DelegateApplicationPack"
        private val realPackageName = "com.ghost.thunder.demo"
    }

}
