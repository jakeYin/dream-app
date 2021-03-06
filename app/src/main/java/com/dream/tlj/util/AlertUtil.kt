package com.dream.tlj.util

import com.dream.tlj.R
import com.dream.tlj.common.AppManager
import net.steamcrafted.loadtoast.LoadToast
import org.xutils.x

object AlertUtil {
    private var lt: LoadToast? = null
    fun showLoading() {
        lt = LoadToast(AppManager.appManager.currentActivity())
        lt!!.setTranslationY(200).setBackgroundColor(x.app().resources.getColor(R.color.colorMain)).setProgressColor(x.app().resources.getColor(R.color.white))
        lt!!.show()
    }

    fun hideLoading() {
        lt!!.success()
        lt!!.hide()
    }
}
