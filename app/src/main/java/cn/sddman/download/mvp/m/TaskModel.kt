package cn.sddman.download.mvp.m

import cn.sddman.download.mvp.e.DownloadTaskEntity

interface TaskModel {
    fun findAllTask(): MutableList<DownloadTaskEntity>?
    fun findTaskByUrl(url: String): MutableList<DownloadTaskEntity>?
    fun findTaskByHash(hash: String): MutableList<DownloadTaskEntity>?
    fun findTaskById(id: Int): DownloadTaskEntity?
    fun findLoadingTask(): MutableList<DownloadTaskEntity>?
    fun findDowningTask(): MutableList<DownloadTaskEntity>?
    fun findSuccessTask(): MutableList<DownloadTaskEntity>?
    fun updateTask(task: DownloadTaskEntity): DownloadTaskEntity?
    fun deleTask(task: DownloadTaskEntity): Boolean
}
