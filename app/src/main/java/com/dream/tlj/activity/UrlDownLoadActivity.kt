package com.dream.tlj.activity

import android.os.Bundle
import android.view.View
import com.dream.tlj.R
import com.dream.tlj.common.BaseActivity
import com.dream.tlj.common.Const
import com.dream.tlj.mvp.p.UrlDownLoadPresenter
import com.dream.tlj.mvp.p.UrlDownLoadPresenterImp
import com.dream.tlj.mvp.v.UrlDownLoadView
import com.dream.tlj.util.Util
import com.xunlei.downloadlib.XLTaskHelper
import kotlinx.android.synthetic.main.activity_url_download.*

class UrlDownLoadActivity : BaseActivity(), UrlDownLoadView {

    private var urlDownLoadPresenter: UrlDownLoadPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_download)
        super.setTopBarTitle(R.string.new_download)
        XLTaskHelper.init(applicationContext)
        urlDownLoadPresenter = UrlDownLoadPresenterImp(this)
    }

    fun startDownloadClick(view: View) {
        urlDownLoadPresenter!!.startTask(url_input!!.text.toString().trim())
    }

    override fun addTaskSuccess() {
        finish()
    }

    override fun addTaskFail(msg: String) {
        Util.alert(this, msg, Const.ERROR_ALERT)
    }
}
