package cn.sddman.download.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sddman.download.R
import cn.sddman.download.activity.PlayerActivity
import cn.sddman.download.activity.SourceDetailActivity
import cn.sddman.download.activity.TorrentInfoActivity
import cn.sddman.download.adapter.DownloadSuccessListAdapter
import cn.sddman.download.common.Const
import cn.sddman.download.common.MessageEvent
import cn.sddman.download.common.Msg
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.p.DownloadSuccessPresenter
import cn.sddman.download.mvp.p.DownloadSuccessPresenterImp
import cn.sddman.download.mvp.v.DownLoadSuccessView
import cn.sddman.download.rule.Rule
import cn.sddman.download.util.FileTools
import cn.sddman.download.util.Util
import com.yanzhenjie.permission.AndPermission
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import kotlinx.android.synthetic.main.frm_download_success.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class DownLoadSuccessFrm : Fragment(), DownLoadSuccessView {
    private var downloadSuccessListAdapter: DownloadSuccessListAdapter? = null
    private var downloadSuccessPresenter: DownloadSuccessPresenter? = null
    private var list: MutableList<DownloadTaskEntity>? = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frm_download_success, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        downloadSuccessPresenter = DownloadSuccessPresenterImp(this)
        delete_button.setOnClickListener {
            val items = arrayOf(context!!.getString(R.string.dele_data_and_file))
            LovelyChoiceDialog(context)
                    .setTopColorRes(R.color.colorMain)
                    .setTitle(R.string.determine_dele)
                    .setIcon(R.drawable.ic_error)
                    .setItemsMultiChoice(items) { positions, items ->
                        val deleFile = items.size > 0
                        downloadSuccessPresenter!!.deleTask(list, deleFile)
                    }.show()
        }

        delete_cancel_button.setOnClickListener {
            toggleDeleteButton()
            downloadSuccessListAdapter?.notifyDataSetChanged()
        }
    }

    override fun toggleDeleteButton() {
        if (delete_bottom_layout.visibility == View.VISIBLE){
            delete_bottom_layout.visibility = View.GONE
        } else {
            delete_bottom_layout.visibility = View.VISIBLE
        }
    }

    override fun deleteState(): Boolean {
        return delete_bottom_layout.visibility == View.VISIBLE
    }

    override fun gotoSource(task: DownloadTaskEntity) {
        val intent = Intent(activity, SourceDetailActivity::class.java)
        intent.putExtra(SourceDetailActivity.DETAIL_URL, task.source)
        intent.putExtra(SourceDetailActivity.TITLE, task.getmFileName())
        intent.putExtra(SourceDetailActivity.MAGNET_RULE, Rule.getRuleById(task.ruleId))
        startActivity(intent)
    }


    override fun initTaskListView(list: MutableList<DownloadTaskEntity>) {
        val manager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        recyclerview!!.layoutManager = manager
        this.list = list
        downloadSuccessListAdapter = DownloadSuccessListAdapter(context!!, this, list)
        recyclerview!!.adapter = downloadSuccessListAdapter
    }

    override fun deleTask(task: DownloadTaskEntity) {
        val items = arrayOf(context!!.getString(R.string.dele_data_and_file))
        LovelyChoiceDialog(context)
                .setTopColorRes(R.color.colorMain)
                .setTitle(R.string.determine_dele)
                .setIcon(R.drawable.ic_error)
                .setItemsMultiChoice(items) { positions, items ->
                    val deleFile = if (items.size > 0) true else false
                    downloadSuccessPresenter!!.deleTask(task, deleFile)
                }.show()
    }

    override fun openFile(task: DownloadTaskEntity) {
        val suffix = Util.getFileSuffix(task.getmFileName()!!)
        val filePath = task.localPath + File.separator + task.getmFileName()
        if (task.file!! && !FileTools.exists(filePath)) {
            task.thumbnailPath = null
            EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_RES_TASK, task)))
            refreshData()
        } else if ("TORRENT" == suffix) {
            val intent = Intent(activity, TorrentInfoActivity::class.java)
            intent.putExtra("torrentPath", filePath)
            intent.putExtra("isDown", true)
            startActivity(intent)
        } else if ("APK" == suffix) {
            val file = File(filePath)
            AndPermission.with(this)
                    .install()
                    .file(file)
                    .start()
        } else if (FileTools.isVideoFile(task.getmFileName())) {
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.VIDEO_PATH, filePath)
            intent.putExtra(PlayerActivity.VIDEO_NAME, task.getmFileName())
            startActivity(intent)
        } else if (!task.file!! && task.taskType == Const.BT_DOWNLOAD) {
            val intent = Intent(activity, TorrentInfoActivity::class.java)
            intent.putExtra("torrentPath", task.url)
            intent.putExtra("isDown", false)
            startActivity(intent)
        }
    }

    override fun alert(msg: String, msgType: Int) {
        Util.alert(this.activity!!, msg, msgType)
        refreshData()
    }

    override fun refreshData() {
        list?.clear()
        list?.addAll(downloadSuccessPresenter!!.downSuccessTaskList!!)
        downloadSuccessListAdapter?.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MessageEvent) {
        val msg = event.message
        if (msg.type == Const.MESSAGE_TYPE_REFRESH_DATA) {
            refreshData()
        }
    }

    override fun onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        super.onStart()
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

}
