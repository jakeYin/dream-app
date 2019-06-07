package cn.sddman.download.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.sddman.download.R
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.p.AppSettingPresenter
import cn.sddman.download.mvp.p.AppSettingPresenterImp
import cn.sddman.download.mvp.v.AppSettingView
import com.ess.filepicker.FilePicker
import com.ess.filepicker.model.EssFile
import kotlinx.android.synthetic.main.activity_app_setting.*


class AppSettingActivity : BaseActivity(), AppSettingView {

    private var appSettingPresenter: AppSettingPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_setting)
        super.setTopBarTitle(getString(R.string.setting))
        appSettingPresenter = AppSettingPresenterImp(this)
        initView()
    }

    private fun initView() {
        mobile_net!!.setOnCheckedChangeListener { compoundButton, b ->
            val net = if (b) Const.MOBILE_NET_OK else Const.MOBILE_NET_NOT
            appSettingPresenter!!.setMobileNet(net.toString() + "")
        }
        down_notify!!.setOnCheckedChangeListener { compoundButton, b ->
            val net = if (b) Const.MOBILE_NET_OK else Const.MOBILE_NET_NOT
            appSettingPresenter!!.setDownNotify(net.toString() + "")
        }
    }

    override fun initSetting(key: String, value: String) {
        if (Const.SAVE_PATH_KEY == key) {
            local_path_text!!.text = value
        } else if (Const.DOWN_COUNT_KEY == key) {
            down_count_text!!.text = value
        } else if (Const.MOBILE_NET_KEY == key) {
            val check = if (value == Const.MOBILE_NET_OK.toString() + "") true else false
            mobile_net!!.isChecked = check
        } else if (Const.DOWN_NOTIFY_KEY == key) {
            val check = if (value == Const.MOBILE_NET_OK.toString() + "") true else false
            down_notify!!.isChecked = check
        }
    }

    private fun setLocalPathClick(view: View) {
        FilePicker.from(this@AppSettingActivity)
                .chooseForFloder()
                .isSingle
                .setMaxCount(0)
                //.setFileTypes("TORRENT")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start()
    }

    fun downCountPlusClick(view: View) {
        var count = Integer.valueOf(down_count_text!!.text.toString())
        if (count < Const.MAX_DOWN_COUNT) {
            count++
            down_count_text.text = count.toString() + ""
            appSettingPresenter!!.setDownCount(count.toString() + "")
        }
    }

    fun downCountCutClick(view: View) {
        var count = Integer.valueOf(down_count_text!!.text.toString())
        if (count > Const.MIN_DOWN_COUNT) {
            count--
            down_count_text.text = count.toString() + ""
            appSettingPresenter!!.setDownCount(count.toString() + "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            val fileList = data!!.getParcelableArrayListExtra<EssFile>(com.ess.filepicker.util.Const.EXTRA_RESULT_SELECTION)
            local_path_text!!.text = fileList[0].absolutePath
            appSettingPresenter!!.setSavePath(fileList[0].absolutePath)
        }
    }

    companion object {
        private val REQUEST_CODE_CHOOSE = 10086
    }

}
