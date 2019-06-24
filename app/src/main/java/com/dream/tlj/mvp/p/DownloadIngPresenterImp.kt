package com.dream.tlj.mvp.p

import com.dream.tlj.common.Const
import com.dream.tlj.fragment.DownProgressNotify
import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.mvp.m.DownLoadModel
import com.dream.tlj.mvp.m.DownLoadModelImp
import com.dream.tlj.mvp.m.TaskModel
import com.dream.tlj.mvp.m.TaskModelImp
import com.dream.tlj.mvp.v.DownLoadIngView
import com.dream.tlj.util.AppSettingUtil
import com.dream.tlj.util.SystemConfig

class DownloadIngPresenterImp(private val downLoadIngView: DownLoadIngView) : DownloadIngPresenter {
    private val taskModel: TaskModel
    private val downLoadModel: DownLoadModel
    override val downloadingTaskList: List<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    private val isLoop = true

    init {
        taskModel = TaskModelImp()
        downLoadModel = DownLoadModelImp()
        downloadingTaskList = taskModel.findLoadingTask()
        //downLoadIngView.initTaskListView(list);
        //refreshData();
    }

    override fun updateTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        taskModel.updateTask(task);
    }

    override fun startTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        val netType = SystemConfig.netType
        if (netType == Const.NET_TYPE_UNKNOW) {
            downLoadIngView.alert("没有网络,下载暂停", Const.ERROR_ALERT)
            return
        } else if ((!AppSettingUtil.instance.isMobileNetDown!!) && netType == Const.NET_TYPE_MOBILE) {
            downLoadIngView.alert("设置不允许允许流量下载,请在设置里开启流量下载", Const.ERROR_ALERT)
            return
        }
        val downCount = AppSettingUtil.instance.downCount
        val downs = taskModel.findDowningTask()
        if (downCount <= downs!!.size) {
            task.setmTaskStatus(Const.DOWNLOAD_WAIT)
            taskModel.updateTask(task)
            return
        }
        if (!downLoadModel.startTask(task))
            downLoadIngView.alert("开始任务失败,无法获取下载资源,可尝试多点几次开始任务", Const.ERROR_ALERT)
    }

    override fun stopTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        downLoadModel.stopTask(task)
    }

    override fun getLoclUrl(task: com.dream.tlj.mvp.e.DownloadTaskEntity):String {
        return downLoadModel.getLoclUrl(task)
    }

    override fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity, deleFile: Boolean) {
        downLoadModel.deleTask(task, true, deleFile)
        DownProgressNotify.instance.cancelDownProgressNotify(task)
    }

    override fun deleTask(tasks: List<com.dream.tlj.mvp.e.DownloadTaskEntity>, deleFile: Boolean) {
        for (x in tasks){
            if (x.check){
                downLoadModel.deleTask(x, true, deleFile)
                DownProgressNotify.instance.cancelDownProgressNotify(x)
            }
        }
    }

    override fun refreshData() {

    }

    override fun stopLoop() {

    }

    override fun clearHandler() {

    }
}
