package com.dream.tlj.mvp.p

import com.dream.tlj.mvp.e.DownloadTaskEntity

interface DownloadIngPresenter {
    val downloadingTaskList: List<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun updateTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun startTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun stopTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun getLoclUrl(task: com.dream.tlj.mvp.e.DownloadTaskEntity):String
    fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity, deleFile: Boolean)
    fun deleTask(tasks: List<com.dream.tlj.mvp.e.DownloadTaskEntity>, deleFile: Boolean)
    fun refreshData()
    fun stopLoop()
    fun clearHandler()
}
