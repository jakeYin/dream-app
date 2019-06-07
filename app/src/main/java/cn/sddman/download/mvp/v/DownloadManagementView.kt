package cn.sddman.download.mvp.v


interface DownloadManagementView {
    fun addTaskSuccess()
    fun addTaskFail(msg: String)
    fun updataApp(version: String, url: String, content: String)
}
