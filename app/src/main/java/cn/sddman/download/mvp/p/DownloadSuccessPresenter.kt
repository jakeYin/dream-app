package cn.sddman.download.mvp.p

import cn.sddman.download.mvp.e.DownloadTaskEntity

interface DownloadSuccessPresenter {
    val downSuccessTaskList: List<DownloadTaskEntity>?
    fun deleTask(task: DownloadTaskEntity, deleFile: Boolean)
    fun deleTask(tasks: MutableList<DownloadTaskEntity>?, deleFile: Boolean)
}
