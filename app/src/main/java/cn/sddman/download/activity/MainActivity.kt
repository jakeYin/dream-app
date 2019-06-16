package cn.sddman.download.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

import com.cocosw.bottomsheet.BottomSheet
import com.ess.filepicker.FilePicker
import com.ess.filepicker.model.EssFile

import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.Event
import org.xutils.view.annotation.ViewInject

import java.util.ArrayList

import cn.sddman.download.R
import cn.sddman.download.common.BaseActivity
import cn.sddman.download.common.Const
import cn.sddman.download.util.Util

@ContentView(R.layout.activity_main)
class MainActivity : BaseActivity() {
    private var bottomSheet: BottomSheet.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomMenu()
    }

    private fun initBottomMenu() {
        bottomSheet = BottomSheet.Builder(this)
                .title(R.string.new_download)
                .sheet(R.menu.down_source)
                .listener { _, which ->
                    when (which) {
                        R.id.qr -> {
                        }
                        R.id.url -> {
                            val intent = Intent(this@MainActivity, UrlDownLoadActivity::class.java)
                            startActivity(intent)
                        }
                        R.id.bt -> FilePicker.from(this@MainActivity)
                                .chooseForBrowser()
                                .isSingle
                                //.setFileTypes("TORRENT")
                                .requestCode(REQUEST_CODE_CHOOSE)
                                .start()
                    }
                }
    }

    @Event(value = R.id.add_download)
    private fun searchClick(view: View) {
        bottomSheet!!.show()
    }

    @Event(value = R.id.down_manage)
    private fun downManageClick(view: View) {
        val intent = Intent(this@MainActivity, DownloadManagementActivity::class.java)
        startActivity(intent)
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
                startActivity(intent)
            } else {
                Util.alert(this@MainActivity, "选择的文件不是种子文件", Const.ERROR_ALERT)
            }

        }
    }

    companion object {

        private val REQUEST_CODE_CHOOSE = 10086
    }


}
