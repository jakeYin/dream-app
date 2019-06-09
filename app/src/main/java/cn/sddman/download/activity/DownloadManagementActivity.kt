package cn.sddman.download.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import cn.sddman.download.R
import cn.sddman.download.common.*
import cn.sddman.download.mvp.p.AppConfigPresenter
import cn.sddman.download.mvp.p.AppConfigPresenterImp
import cn.sddman.download.mvp.p.DownloadManagementPresenter
import cn.sddman.download.mvp.p.DownloadManagementPresenterImp
import cn.sddman.download.mvp.v.DownloadManagementView
import cn.sddman.download.service.DownService
import cn.sddman.download.util.AppConfigUtil
import cn.sddman.download.util.Util
import cn.sddman.download.view.DownLoadIngFrm
import cn.sddman.download.view.DownLoadSuccessFrm
import com.cocosw.bottomsheet.BottomSheet
import com.ess.filepicker.FilePicker
import com.ess.filepicker.model.EssFile
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.common.Constant
import kotlinx.android.synthetic.main.activity_download_management.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class DownloadManagementActivity : BaseActivity(), DownloadManagementView {
    private val mFragments = ArrayList<Fragment>()
    private var bottomSheet: BottomSheet.Builder? = null

    private var downloadManagementPresenter: DownloadManagementPresenter? = null
    private var appConfigPresenter: AppConfigPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_management)
        val intent = Intent(this, DownService::class.java)
        startService(intent)
        downloadManagementPresenter = DownloadManagementPresenterImp(this)
        appConfigPresenter = AppConfigPresenterImp()
        initViewPage()
        initBottomMenu()
    }

    private fun initViewPage() {
        val downLoadSuccessFrm = DownLoadSuccessFrm()
        val downLoadIngFrm = DownLoadIngFrm()
        mFragments.add(downLoadIngFrm)
        mFragments.add(downLoadSuccessFrm)
        viewPager!!.offscreenPageLimit = 2
        viewPager.adapter = CusAdapter(supportFragmentManager, mFragments)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                changeTab(i)
            }
            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    private fun changeTab(index: Int) {
        if (index == 0) {
            downloading!!.setTextColor(resources.getColor(R.color.white))
            downloadfinish!!.setTextColor(resources.getColor(R.color.trwhite))
        } else {
            downloading!!.setTextColor(resources.getColor(R.color.trwhite))
            downloadfinish!!.setTextColor(resources.getColor(R.color.white))
        }
        EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_REFRESH_DATA)))
    }

    fun downloadingClick(view: View) {
        viewPager!!.currentItem = 0
    }

    fun downloadfinishClick(view: View) {
        viewPager!!.currentItem = 1
    }

    fun addTaskClick(view: View) {
        bottomSheet!!.show()
    }

    fun appSettingClick(view: View) {
        intent = Intent(this@DownloadManagementActivity, AppSettingActivity::class.java)
        startActivity(intent)
    }

    fun magnetSearchClick(view: View) {
//        intent = Intent(this@DownloadManagementActivity, MagnetSearchActivity::class.java)
//        startActivity(intent)
        intent = Intent(this@DownloadManagementActivity, SourceActivity::class.java)
        startActivity(intent)
    }

    private fun initBottomMenu() {
        bottomSheet = BottomSheet.Builder(this)
                .title(R.string.new_download)
                .sheet(R.menu.down_source)
                .listener { dialog, which ->
                    when (which) {
                        R.id.qr -> {
                            intent = Intent(this@DownloadManagementActivity, CaptureActivity::class.java)
                            startActivityForResult(intent, REQUEST_CODE_SCAN)
                        }
                        R.id.url -> {
                            intent = Intent(this@DownloadManagementActivity, UrlDownLoadActivity::class.java)
                            startActivity(intent)
                        }
                        R.id.bt -> FilePicker.from(this@DownloadManagementActivity)
                                .chooseForBrowser()
                                //.chooseForFloder()
                                .isSingle
                                //.setMaxCount(0)
                                //.setFileTypes("TORRENT")
                                .requestCode(REQUEST_CODE_CHOOSE)
                                .start()
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            val fileList = data!!.getParcelableArrayListExtra<EssFile>(com.ess.filepicker.util.Const.EXTRA_RESULT_SELECTION)
            val suffix = fileList[0].name.substring(fileList[0].name.lastIndexOf(".") + 1).toUpperCase()
            if ("TORRENT" == suffix) {
                val intent = Intent(this, TorrentInfoActivity::class.java)
                intent.putExtra("torrentPath", fileList[0].absolutePath)
                intent.putExtra("isDown", true)
                startActivity(intent)
            } else {
                Util.alert(this@DownloadManagementActivity, "选择的文件不是种子文件", Const.ERROR_ALERT)
            }

        } else if (requestCode == REQUEST_CODE_SCAN) {
            val content = data!!.getStringExtra(Constant.CODED_CONTENT)
            LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.colorMain)
                    .setIcon(R.drawable.ic_success)
                    .setButtonsColorRes(R.color.colorMain)
                    .setTitle("创建任务")
                    .setMessage(content)
                    .setPositiveButton(android.R.string.ok) { downloadManagementPresenter!!.startTask(content) }
                    .show()
        }
    }


    override fun addTaskSuccess() {
        // Intent intent =new Intent(UrlDownLoadActivity.this,DownloadManagementActivity.class);
        // startActivity(intent);
        // finish();
    }

    override fun addTaskFail(msg: String) {
        Util.alert(this, msg, Const.ERROR_ALERT)
    }

    override fun updataApp(version: String, url: String, content: String) {
        LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorMain)
                .setIcon(R.drawable.ic_success)
                .setButtonsColorRes(R.color.colorMain)
                .setTitle("App有更新")
                .setMessage("当前版本：" + AppConfigUtil.localVersionName + "，最新版本：" + version +
                        "\n" + content)
                .setPositiveButton("确定下载更新") { downloadManagementPresenter!!.startTask(url) }
                .show()
    }

    companion object {
        private val REQUEST_CODE_CHOOSE = 10086
        private val REQUEST_CODE_SCAN = 10010
    }
}
