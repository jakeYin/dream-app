package cn.sddman.download.mvp.p

import android.os.Handler
import android.os.Looper
import android.os.Message

import com.xunlei.downloadlib.XLTaskHelper
import com.xunlei.downloadlib.parameter.TorrentInfo
import com.xunlei.downloadlib.parameter.XLTaskInfo

import org.xutils.x

import java.io.File

import cn.sddman.download.R
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.m.DownLoadModel
import cn.sddman.download.mvp.m.DownLoadModelImp
import cn.sddman.download.mvp.m.TaskModel
import cn.sddman.download.mvp.m.TaskModelImp
import cn.sddman.download.mvp.v.DownLoadIngView
import cn.sddman.download.mvp.v.DownLoadSuccessView
import cn.sddman.download.util.FileTools
import cn.sddman.download.util.Util

class DownloadSuccessPresenterImp(private val downLoadSuccessView: DownLoadSuccessView) : DownloadSuccessPresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel
    private var list: MutableList<DownloadTaskEntity>? = arrayListOf()
    override val downSuccessTaskList: List<DownloadTaskEntity>?
        get() {
            list = taskModel.findSuccessTask()
            return list
        }


    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
        list = taskModel.findSuccessTask()
        list?.let { downLoadSuccessView.initTaskListView(it) }
    }


    override fun deleTask(task: DownloadTaskEntity, deleFile: Boolean) {
        val b = downLoadModel.deleTask(task, deleFile)
        if (b!!) {
            downLoadSuccessView.refreshData()
            downLoadSuccessView.alert(x.app().getString(R.string.dele_success), Const.SUCCESS_ALERT)
        } else {
            downLoadSuccessView.alert(x.app().getString(R.string.dele_fail), Const.ERROR_ALERT)
        }
    }


}
