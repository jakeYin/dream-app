package cn.sddman.download.activity

import android.os.Bundle
import android.view.View
import cn.sddman.download.R
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.p.UrlDownLoadPresenter
import cn.sddman.download.mvp.p.UrlDownLoadPresenterImp
import cn.sddman.download.mvp.v.UrlDownLoadView
import cn.sddman.download.util.Util
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
        // Intent intent =new Intent(UrlDownLoadActivity.this,DownloadManagementActivity.class);
        // startActivity(intent);
        finish()
    }

    override fun addTaskFail(msg: String) {
        Util.alert(this, msg, Const.ERROR_ALERT)
    }
}
