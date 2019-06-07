package cn.sddman.download.mvp.p

import org.xutils.x

import cn.sddman.download.R
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.m.DownLoadModel
import cn.sddman.download.mvp.m.DownLoadModelImp
import cn.sddman.download.mvp.m.TaskModel
import cn.sddman.download.mvp.m.TaskModelImp
import cn.sddman.download.mvp.v.DownloadManagementView

class DownloadManagementPresenterImp(private val downloadManagementView: DownloadManagementView) : DownloadManagementPresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel

    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
    }

    override fun startTask(url: String) {
        val tasks = taskModel.findTaskByUrl(url)
        if (tasks != null && tasks.size > 0) {
            val task = tasks[0]
            if (task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION
                    || task.getmTaskStatus() == Const.DOWNLOAD_LOADING
                    || task.getmTaskStatus() == Const.DOWNLOAD_FAIL
                    || task.getmTaskStatus() == Const.DOWNLOAD_STOP) {
                downloadManagementView.addTaskFail(x.app().getString(R.string.task_earlier_has))
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_SUCCESS) {
                downloadManagementView.addTaskFail(x.app().getString(R.string.task_earlier_success))
            }
        } else {
            val b = downLoadModel.startUrlTask(url)
            if (b!!)
                downloadManagementView.addTaskSuccess()
            else
                downloadManagementView.addTaskFail(x.app().getString(R.string.add_task_fail))

        }
    }
}
