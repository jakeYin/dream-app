package com.dream.tlj.mvp.p

import com.dream.tlj.R
import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.mvp.m.DownLoadModel
import com.dream.tlj.mvp.m.DownLoadModelImp
import com.dream.tlj.mvp.m.TaskModel
import com.dream.tlj.mvp.m.TaskModelImp
import com.dream.tlj.mvp.v.DownLoadSuccessView
import org.xutils.x

class DownloadSuccessPresenterImp(private val downLoadSuccessView: DownLoadSuccessView) : DownloadSuccessPresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel
    private var list: MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>? = arrayListOf()
    override val downSuccessTaskList: List<com.dream.tlj.mvp.e.DownloadTaskEntity>?
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


    override fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity, deleFile: Boolean) {
        val b = downLoadModel.deleTask(task, deleFile)
        if (b!!) {
            downLoadSuccessView.refreshData()
            downLoadSuccessView.alert(x.app().getString(R.string.dele_success), Const.SUCCESS_ALERT)
        } else {
            downLoadSuccessView.alert(x.app().getString(R.string.dele_fail), Const.ERROR_ALERT)
        }
    }

    override fun deleTask(tasks: MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?, deleFile: Boolean) {
        if (tasks != null) {
            for (x in tasks){
                if (x.check){
                    downLoadModel.deleTask(x, deleFile)
                }
            }
            downLoadSuccessView.toggleDeleteButton()
            downLoadSuccessView.refreshData()
            downLoadSuccessView.alert(x.app().getString(R.string.dele_success), Const.SUCCESS_ALERT)
        }
    }


}
