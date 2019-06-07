package cn.sddman.download.mvp.p

import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.e.TorrentInfoEntity

interface DownloadSuccessPresenter {
    val downSuccessTaskList: List<DownloadTaskEntity>?
    fun deleTask(task: DownloadTaskEntity, deleFile: Boolean)
}
