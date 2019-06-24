package com.dream.tlj.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dream.tlj.R
import com.dream.tlj.activity.PlayerActivity
import com.dream.tlj.activity.SourceDetailActivity
import com.dream.tlj.activity.TorrentInfoActivity
import com.dream.tlj.adapter.DownloadingListAdapter
import com.dream.tlj.common.Const
import com.dream.tlj.common.MessageEvent
import com.dream.tlj.common.Msg
import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.mvp.p.DownloadIngPresenter
import com.dream.tlj.mvp.p.DownloadIngPresenterImp
import com.dream.tlj.mvp.v.DownLoadIngView
import com.dream.tlj.rule.Rule
import com.dream.tlj.util.FileTools
import com.dream.tlj.util.Util
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

class DownLoadIngFrm : Fragment(), DownLoadIngView {
    private var downloadIngPresenter: DownloadIngPresenter? = null
    private var downloadingListAdapter: DownloadingListAdapter? = null
    private val list = ArrayList<com.dream.tlj.mvp.e.DownloadTaskEntity>()
    private lateinit var delete_button:View
    private lateinit var delete_cancel_button:View
    private lateinit var delete_bottom_layout:View
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frm_download_ing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        downloadIngPresenter = DownloadIngPresenterImp(this)
        delete_button.setOnClickListener {
            LovelyStandardDialog(context)
                    .setTopColorRes(R.color.colorMain)
                    .setTitle(R.string.determine_dele)
                    .setIcon(R.drawable.ic_error)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.ok) {
                        downloadIngPresenter!!.deleTask(list, true)
                    }.show()
        }

        delete_cancel_button.setOnClickListener {
            toggleDeleteButton()
            downloadingListAdapter?.notifyDataSetChanged()
        }

    }

    private fun initView() {
        recyclerView = view!!.findViewById(R.id.recyclerview)
        delete_button = view!!.findViewById(R.id.delete_button)
        delete_cancel_button = view!!.findViewById(R.id.delete_cancel_button)
        delete_bottom_layout = view!!.findViewById(R.id.delete_bottom_layout)
        val manager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager
        downloadingListAdapter = DownloadingListAdapter(context!!, this, this.list)
        recyclerView!!.adapter = downloadingListAdapter
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

    override fun startTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        downloadIngPresenter!!.startTask(task)
    }

    override fun updateTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        downloadIngPresenter!!.updateTask(task)
    }

    override fun stopTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        downloadIngPresenter!!.stopTask(task)
    }

    override fun openFile(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        val suffix = task.getmFileName()!!.substring(task.getmFileName()!!.lastIndexOf(".") + 1).toUpperCase()
        if ("TORRENT" == suffix) {
            val intent = Intent(activity, TorrentInfoActivity::class.java)
            intent.putExtra("torrentPath", task.localPath + File.separator + task.getmFileName())
            intent.putExtra("isDown", true)
            startActivity(intent)
        } else if (FileTools.isVideoFile(task.getmFileName())) {
            if (task!!.getmTaskStatus() == Const.DOWNLOAD_STOP ||
                    task!!.getmTaskStatus() == Const.DOWNLOAD_CONNECTION && task!!.taskId == 0L ||
                    task!!.getmTaskStatus() == Const.DOWNLOAD_WAIT) {
                downloadIngPresenter?.startTask(task!!)
            }
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

    override fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        LovelyStandardDialog(context)
                .setTopColorRes(R.color.colorMain)
                .setTitle(R.string.determine_dele)
                .setIcon(R.drawable.ic_error)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.ok) {
                    downloadIngPresenter!!.deleTask(task, true)
                }.show()
    }

    override fun refreshData(tasks: List<com.dream.tlj.mvp.e.DownloadTaskEntity>) {
        list.clear()
        list.addAll(tasks)
        downloadingListAdapter!!.notifyDataSetChanged()
    }

    override fun alert(msg: String, msgType: Int) {
        Util.alert(this.activity!!, msg, msgType)
    }

    override fun gotoSource(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        val intent = Intent(activity, SourceDetailActivity::class.java)
        intent.putExtra(SourceDetailActivity.DETAIL_URL, task.source)
        intent.putExtra(SourceDetailActivity.TITLE, task.getmFileName())
        intent.putExtra(SourceDetailActivity.MAGNET_RULE, Rule.getRuleById(task.ruleId))
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MessageEvent) {
        val msg = event.message
        if (msg.type == Const.MESSAGE_TYPE_RES_TASK) {
            val task = msg.obj as com.dream.tlj.mvp.e.DownloadTaskEntity
            downloadIngPresenter!!.startTask(task)
            EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_SWITCH_TAB, 0)))
        } else if (msg.type == Const.MESSAGE_TYPE_APP_UPDATA_PRESS) {
            val tasks = msg.obj as List<com.dream.tlj.mvp.e.DownloadTaskEntity>
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
