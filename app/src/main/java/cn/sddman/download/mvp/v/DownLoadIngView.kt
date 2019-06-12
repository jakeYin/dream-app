package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.DownloadTaskEntity

interface DownLoadIngView {
    fun updateTask(task: DownloadTaskEntity)
    fun startTask(task: DownloadTaskEntity)
    fun stopTask(task: DownloadTaskEntity)
    fun openFile(task: DownloadTaskEntity)
    fun deleTask(task: DownloadTaskEntity)
    fun refreshData(tasks: List<DownloadTaskEntity>)
    fun toggleDeleteButton()
    fun deleteState():Boolean
    fun alert(msg: String, msgType: Int)
}
