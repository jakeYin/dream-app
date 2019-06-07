package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.DownloadTaskEntity

interface DownLoadIngView {
    fun startTask(task: DownloadTaskEntity)
    fun stopTask(task: DownloadTaskEntity)
    fun openFile(task: DownloadTaskEntity)
    fun deleTask(task: DownloadTaskEntity)
    fun refreshData(tasks: List<DownloadTaskEntity>)
    fun alert(msg: String, msgType: Int)
}
