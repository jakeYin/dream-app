package cn.sddman.download.mvp.v

import cn.sddman.download.mvp.e.DownloadTaskEntity

interface DownLoadSuccessView {
    fun initTaskListView(list: MutableList<DownloadTaskEntity>)
    fun deleTask(task: DownloadTaskEntity)
    fun openFile(task: DownloadTaskEntity)
    fun refreshData()
    fun alert(msg: String, msgType: Int)
    fun toggleDeleteButton()
    fun deleteState():Boolean
    fun gotoSource(task: DownloadTaskEntity)
}
