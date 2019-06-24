package com.dream.tlj.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.FragmentActivity
import com.dream.tlj.R
import com.dream.tlj.common.Const
import com.irozon.sneaker.Sneaker
import org.xutils.x

object Util {

    fun alert(activity: Activity, msg: String, msgType: Int) {
        if (Const.ERROR_ALERT == msgType) {
            Sneaker.with(activity)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_error, R.color.white, false)
                    .sneak(R.color.colorAccent)
        } else if (Const.SUCCESS_ALERT == msgType) {
            Sneaker.with(activity)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_success, R.color.white, false)
                    .sneak(R.color.success)
        } else if (Const.WARNING_ALERT == msgType) {
            Sneaker.with(activity)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_warning, R.color.white, false)
                    .sneak(R.color.warning)
        }
    }

    fun alert(activity: FragmentActivity, msg: String, msgType: Int) {
        if (Const.ERROR_ALERT == msgType) {
            Sneaker.with(activity)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_error, R.color.white, false)
                    .sneak(R.color.colorAccent)
        } else if (Const.SUCCESS_ALERT == msgType) {
            Sneaker.with(activity)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_success, R.color.white, false)
                    .sneak(R.color.success)
        } else if (Const.WARNING_ALERT == msgType) {
            Sneaker.with(activity)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_warning, R.color.white, false)
                    .sneak(R.color.warning)
        }
    }

    fun checkApkExist(packageName: String): Boolean {
        if (StringUtil.isEmpty(packageName))
            return false
        try {
            val info = x.app().applicationContext.packageManager
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }

    fun putTextIntoClip(text: String) {
        val clipboardManager = x.app().applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(text, text)
        clipboardManager.primaryClip = clipData
    }

    fun getFileSuffix(name: String): String {
        return name.substring(name.lastIndexOf(".") + 1).toUpperCase()
    }


}
