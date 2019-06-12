package cn.sddman.download.mvp.p

import cn.sddman.download.mvp.e.DownloadTaskEntity

interface DownloadIngPresenter {
    val downloadingTaskList: List<DownloadTaskEntity>?
    fun updateTask(task: DownloadTaskEntity)
    fun startTask(task: DownloadTaskEntity)
    fun stopTask(task: DownloadTaskEntity)
    fun getLoclUrl(task: DownloadTaskEntity):String
    fun deleTask(task: DownloadTaskEntity, deleFile: Boolean)
    fun deleTask(tasks: List<DownloadTaskEntity>, deleFile: Boolean)
    fun refreshData()
    fun stopLoop()
    fun clearHandler()
}
