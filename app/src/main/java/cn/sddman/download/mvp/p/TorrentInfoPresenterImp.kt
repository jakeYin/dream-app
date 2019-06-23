package cn.sddman.download.mvp.p

import cn.sddman.download.R
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.TorrentInfoEntity
import cn.sddman.download.mvp.m.DownLoadModel
import cn.sddman.download.mvp.m.DownLoadModelImp
import cn.sddman.download.mvp.m.TaskModel
import cn.sddman.download.mvp.m.TaskModelImp
import cn.sddman.download.mvp.v.TorrentInfoView
import cn.sddman.download.util.FileTools
import com.xunlei.downloadlib.XLTaskHelper
import org.xutils.x
import java.io.File

class TorrentInfoPresenterImp(private val torrentInfoView: TorrentInfoView, private val torrentPath: String) : TorrentInfoPresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel
    private var list: List<TorrentInfoEntity>? = null

    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
        list = downLoadModel.getTorrentInfo(torrentPath)
        torrentInfoView.initTaskListView(list!!)

    }

    override fun startTask(checkList: List<TorrentInfoEntity>) {
        //String path=task.getLocalPath()+ File.separator+task.getmFileName();
        val torrentInfo = XLTaskHelper.instance(x.app().applicationContext).getTorrentInfo(torrentPath)
        val tasks = taskModel.findTaskByHash(torrentInfo.mInfoHash)
        if (tasks != null && tasks.size > 0) {
            val task = tasks[0]
            if (!FileTools.exists(task.localPath + File.separator + task.getmFileName())) {
                downLoadModel.startTorrentTask(task)
                torrentInfoView.startTaskSuccess()
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION
                    || task.getmTaskStatus() == Const.DOWNLOAD_LOADING
                    || task.getmTaskStatus() == Const.DOWNLOAD_FAIL
                    || task.getmTaskStatus() == Const.DOWNLOAD_STOP
                    || task.getmTaskStatus() == Const.DOWNLOAD_WAIT) {
                torrentInfoView.startTaskFail(x.app().getString(R.string.task_earlier_has))
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_SUCCESS) {
                torrentInfoView.startTaskFail(x.app().getString(R.string.task_earlier_success))
            }
        } else {
            downLoadModel.startTorrentTask(torrentPath)
            torrentInfoView.startTaskSuccess()
        }
    }
}
