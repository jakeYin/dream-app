package cn.sddman.download.thread

import android.content.Intent
import android.provider.MediaStore

import com.xunlei.downloadlib.XLTaskHelper

import org.greenrobot.eventbus.EventBus
import org.xutils.x

import java.io.File
import java.util.ArrayList

import cn.sddman.download.activity.TorrentInfoActivity
import cn.sddman.download.common.AppManager
import cn.sddman.download.common.Const
import cn.sddman.download.common.MessageEvent
import cn.sddman.download.common.Msg
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.m.DownLoadModel
import cn.sddman.download.mvp.m.DownLoadModelImp
import cn.sddman.download.mvp.m.TaskModel
import cn.sddman.download.mvp.m.TaskModelImp
import cn.sddman.download.util.AppSettingUtil
import cn.sddman.download.util.DownUtil
import cn.sddman.download.util.FileTools
import cn.sddman.download.util.SystemConfig
import cn.sddman.download.view.DownProgressNotify

class DownUpdateUI {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel
    var tasks: List<DownloadTaskEntity>? = null
        private set

    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
        tasks = ArrayList()
    }

    fun downUpdate() {
        val taskModel = TaskModelImp()
        val downLoadModel = DownLoadModelImp()
        tasks = taskModel.findLoadingTask()
        if (tasks != null) {
            if (!AppSettingUtil.instance.isDown!!) {
                //downLoadIngView.alert("没有网络,下载暂停", Const.ERROR_ALERT);
                for (task in tasks!!) {
                    if (task.getmTaskStatus() != Const.DOWNLOAD_STOP) {
                        downLoadModel.stopTask(task)
                        task.setmTaskStatus(Const.DOWNLOAD_WAIT)
                        taskModel.updateTask(task)
                    }
                }
            } else {
                val downCount = AppSettingUtil.instance.downCount
                val downs = taskModel.findDowningTask()
                var wait = if (downs == null) 0 else downs.size - downCount
                for (task in tasks!!) {
                    if (wait > 0) {
                        if (task.getmTaskStatus() != Const.DOWNLOAD_WAIT && task.getmTaskStatus() != Const.DOWNLOAD_FAIL) {
                            wait--
                            downLoadModel.stopTask(task)
                            task.setmTaskStatus(Const.DOWNLOAD_WAIT)
                            taskModel.updateTask(task)
                            continue
                        }
                    }
                    if (task.getmTaskStatus() != Const.DOWNLOAD_STOP && task.getmTaskStatus() != Const.DOWNLOAD_WAIT && task.taskId != 0L) {
                        val taskInfo = XLTaskHelper.instance(x.app().applicationContext).getTaskInfo(task.taskId)
                        task.taskId = taskInfo.mTaskId
                        task.setmTaskStatus(taskInfo.mTaskStatus)
                        task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed)
                        task.setmDownloadSpeed(taskInfo.mDownloadSpeed)
                        if (taskInfo.mTaskId != 0L) {
                            task.setmFileSize(taskInfo.mFileSize)
                            task.setmDownloadSize(taskInfo.mDownloadSize)
                        }
                        taskModel.updateTask(task)
                        if (DownUtil.isDownSuccess(task)) {
                            downLoadModel.stopTask(task)
                            task.setmTaskStatus(Const.DOWNLOAD_SUCCESS)
                            taskModel.updateTask(task)
                            EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_REFRESH_DATA, task)))
                            val suffix = task.getmFileName()!!.substring(task.getmFileName()!!.lastIndexOf(".") + 1).toUpperCase()
                            if ("TORRENT" == suffix) {
                                openTorrentFile(task)
                            }
                        }
                        if (AppSettingUtil.instance.isShowDownNotify!!) {
                            DownProgressNotify.instance.createDowneProgressNotify(task)
                            DownProgressNotify.instance.updateDownProgressNotify(task)
                        } else {
                            DownProgressNotify.instance.cancelDownProgressNotify(task)
                        }
                    } else {
                        DownProgressNotify.instance.cancelDownProgressNotify(task)
                        if (wait < 0 && task.getmTaskStatus() == Const.DOWNLOAD_WAIT) {
                            downLoadModel.startTask(task)
                        }
                    }
                }
            }
//            getDownMovieThumbnails()
            EventBus.getDefault().postSticky(MessageEvent(Msg(Const.MESSAGE_TYPE_APP_UPDATA_PRESS, tasks!!)))
            //downLoadIngView.refreshData(tasks);
        }
    }

    fun getDownMovieThumbnails() {
        tasks = taskModel.findAllTask()
        val tasks: List<DownloadTaskEntity>? = tasks
        if (tasks != null) {
            for (task in tasks) {
                val filePath = task.localPath + File.separator + task.getmFileName()

                if (FileTools.isVideoFile(task.getmFileName())){
                    if (task.thumbnailPath == null || FileTools.exists(task.thumbnailPath!!)){
                        val bitmap = FileTools.getVideoThumbnail(filePath, 250, 150, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
                        if (bitmap != null) {
                            val thumbnailPath = FileTools.saveBitmap(bitmap, System.currentTimeMillis().toString() + ".jpg")
                            task.thumbnailPath = thumbnailPath
                            taskModel.updateTask(task)
                        }
                    }
                }
            }
        }
    }

    fun openTorrentFile(task: DownloadTaskEntity) {
        val suffix = task.getmFileName()!!.substring(task.getmFileName()!!.lastIndexOf(".") + 1).toUpperCase()
        if ("TORRENT" == suffix) {
            val intent = Intent(AppManager.appManager.currentActivity(), TorrentInfoActivity::class.java)
            intent.putExtra("torrentPath", task.localPath + File.separator + task.getmFileName())
            intent.putExtra("isDown", true)
            AppManager.appManager.currentActivity().startActivity(intent)
        }
    }

    companion object {
        private var updateUI: DownUpdateUI? = null
        val instance: DownUpdateUI
            @Synchronized get() {
                if (updateUI == null) {
                    updateUI = DownUpdateUI()
                }
                return updateUI!!
            }
    }
}
