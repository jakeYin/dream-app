package cn.sddman.download.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.yarolegovich.lovelydialog.LovelyChoiceDialog

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.io.File
import java.util.ArrayList

import cn.sddman.download.R
import cn.sddman.download.activity.PlayerActivity
import cn.sddman.download.activity.TorrentInfoActivity
import cn.sddman.download.adapter.DownloadingListAdapter
import cn.sddman.download.common.Const
import cn.sddman.download.common.MessageEvent
import cn.sddman.download.common.Msg
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.p.DownloadIngPresenter
import cn.sddman.download.mvp.p.DownloadIngPresenterImp
import cn.sddman.download.mvp.v.DownLoadIngView
import cn.sddman.download.util.FileTools
import cn.sddman.download.util.Util

class DownLoadIngFrm : Fragment(), DownLoadIngView {
    private var recyclerView: RecyclerView? = null
    private var downloadIngPresenter: DownloadIngPresenter? = null
    private var downloadingListAdapter: DownloadingListAdapter? = null
    private val list = ArrayList<DownloadTaskEntity>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frm_download_ing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        downloadIngPresenter = DownloadIngPresenterImp(this)

    }

    private fun initView() {
        recyclerView = view!!.findViewById(R.id.recyclerview)
        val manager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager
        downloadingListAdapter = DownloadingListAdapter(context!!, this, this.list)
        recyclerView!!.adapter = downloadingListAdapter
    }

    override fun startTask(task: DownloadTaskEntity) {
        downloadIngPresenter!!.startTask(task)
    }

    override fun stopTask(task: DownloadTaskEntity) {
        downloadIngPresenter!!.stopTask(task)
    }

    override fun openFile(task: DownloadTaskEntity) {
        val suffix = task.getmFileName()!!.substring(task.getmFileName()!!.lastIndexOf(".") + 1).toUpperCase()
        if ("TORRENT" == suffix) {
            val intent = Intent(activity, TorrentInfoActivity::class.java)
            intent.putExtra("torrentPath", task.localPath + File.separator + task.getmFileName())
            intent.putExtra("isDown", true)
            startActivity(intent)
        } else if (FileTools.isVideoFile(task.getmFileName())) {
            downloadIngPresenter!!.startTask(task)
            val localUrl = downloadIngPresenter?.getLoclUrl(task)
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.VIDEO_PATH, localUrl)
            intent.putExtra(PlayerActivity.VIDEO_NAME, task.getmFileName())
            startActivity(intent)
        } else if (!task.file!! && task.taskType == Const.BT_DOWNLOAD) {
            val intent = Intent(activity, TorrentInfoActivity::class.java)
            intent.putExtra("torrentPath", task.url)
            intent.putExtra("isDown", false)
            startActivity(intent)
        }
    }

    override fun deleTask(task: DownloadTaskEntity) {
        val items = arrayOf(context!!.getString(R.string.dele_data_and_file))
        LovelyChoiceDialog(context)
                .setTopColorRes(R.color.colorMain)
                .setTitle(R.string.determine_dele)
                .setIcon(R.drawable.ic_error)
                .setItemsMultiChoice(items) { positions, items ->
                    val deleFile = if (items.size > 0) true else false
                    downloadIngPresenter!!.deleTask(task, deleFile)
                }.show()
    }

    override fun refreshData(tasks: List<DownloadTaskEntity>) {
        list.clear()
        list.addAll(tasks)
        downloadingListAdapter!!.notifyDataSetChanged()
    }

    override fun alert(msg: String, msgType: Int) {
        Util.alert(this.activity!!, msg, msgType)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MessageEvent) {
        val msg = event.message
        if (msg.type == Const.MESSAGE_TYPE_RES_TASK) {
            val task = msg.obj as DownloadTaskEntity
            downloadIngPresenter!!.startTask(task)
            EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_SWITCH_TAB, 0)))
        } else if (msg.type == Const.MESSAGE_TYPE_APP_UPDATA_PRESS) {
            val tasks = msg.obj as List<DownloadTaskEntity>
            refreshData(tasks)
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
        downloadIngPresenter!!.stopLoop()
        downloadIngPresenter!!.clearHandler()
        super.onDestroy()
    }
}
