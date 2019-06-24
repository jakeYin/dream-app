package com.dream.tlj.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.dream.tlj.R
import com.dream.tlj.common.*
import com.dream.tlj.fragment.DownLoadIngFrm
import com.dream.tlj.fragment.DownLoadSuccessFrm
import com.dream.tlj.mvp.p.AppConfigPresenter
import com.dream.tlj.mvp.p.AppConfigPresenterImp
import com.dream.tlj.mvp.p.DownloadManagementPresenter
import com.dream.tlj.mvp.p.DownloadManagementPresenterImp
import com.dream.tlj.mvp.v.DownloadManagementView
import com.dream.tlj.service.DownService
import com.dream.tlj.update.XdUpdateAgent
import com.dream.tlj.util.AppConfigUtil
import com.dream.tlj.util.SharedPreferencesUtils
import com.dream.tlj.util.Util
import com.cocosw.bottomsheet.BottomSheet
import com.dream.tlj.common.*
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
    private var exitTime: Long = 0
    private var downloadManagementPresenter: DownloadManagementPresenter? = null
    private var appConfigPresenter: AppConfigPresenter? = null

    companion object {
        private val REQUEST_CODE_CHOOSE = 10086
        private val REQUEST_CODE_SCAN = 10010
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_management)
        val intent = Intent(this, DownService::class.java)
        startService(intent)
        downloadManagementPresenter = DownloadManagementPresenterImp(this)
        appConfigPresenter = AppConfigPresenterImp()
        initViewPage()
        initBottomMenu()
        showNoteDialog()
        initUpdate()
    }

    private fun showNoteDialog() {
        val isAgreen = SharedPreferencesUtils.getParam(this, "agree_note", false) as Boolean
        if (!isAgreen) {
            AlertDialog.Builder(this).setTitle(R.string.nav_note).setMessage(R.string.note_detail).setPositiveButton(R.string.agreen, DialogInterface.OnClickListener { dialogInterface, i ->
                SharedPreferencesUtils.setParam(this, "agree_note", true)
            }).setNegativeButton(R.string.not_agreen, DialogInterface.OnClickListener { dialogInterface, i -> finish() }).show()
        }
    }

    private fun initUpdate() {
        val updateAgent = XdUpdateAgent.Builder()
                .setDebugMode(false)    //是否显示调试信息(可选,默认:false)
                .setJsonUrl(Const.APK_UPDATE_JSON_URL)    //设置通过其他途径得到的XdUpdateBean(2选1)
                .setShowDialogIfWifi(true)    //设置在WiFi下直接弹出AlertDialog而不使用Notification(可选,默认:false)
                .setDownloadText("立即下载")    //可选,默认为左侧所示的文本
                .setInstallText("立即安装(已下载)")
                .setLaterText("以后再说")
                .setHintText("版本更新")
                .setDownloadingText("正在下载")
                .build()
        updateAgent.update(this)
    }


    private fun initViewPage() {
        val downLoadSuccessFrm = DownLoadSuccessFrm()
        val downLoadIngFrm = DownLoadIngFrm()
        mFragments.add(downLoadIngFrm)
        mFragments.add(downLoadSuccessFrm)
        view_pager!!.offscreenPageLimit = 2
        view_pager.adapter = CusAdapter(supportFragmentManager, mFragments)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
            download_finish!!.setTextColor(resources.getColor(R.color.trwhite))
        } else {
            downloading!!.setTextColor(resources.getColor(R.color.trwhite))
            download_finish!!.setTextColor(resources.getColor(R.color.white))
        }
        EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_REFRESH_DATA)))
    }

    fun downloadingClick(view: View) {
        view_pager!!.currentItem = 0
    }

    fun downloadfinishClick(view: View) {
        view_pager!!.currentItem = 1
    }

    fun addTaskClick(view: View) {
        bottomSheet!!.show()
    }

    fun appSettingClick(view: View) {
        intent = Intent(this@DownloadManagementActivity, AppSettingActivity::class.java)
        startActivity(intent)
    }

    fun sourceClick(view: View) {
        intent = Intent(this@DownloadManagementActivity, SourceActivity::class.java)
        intent.putExtra(SourceActivity.TYPE,SourceActivity.TYPE_SOURCE)
        startActivity(intent)
    }

    fun magnetSearchClick(view: View) {
        intent = Intent(this@DownloadManagementActivity, SourceActivity::class.java)
        intent.putExtra(SourceActivity.TYPE,SourceActivity.TYPE_SEARCH)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun exit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this@DownloadManagementActivity, "再按一次退出", Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            finish()
            System.exit(0)
        }
    }

}
