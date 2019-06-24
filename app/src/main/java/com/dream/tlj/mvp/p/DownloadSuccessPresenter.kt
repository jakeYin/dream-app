package com.dream.tlj.mvp.p

import com.dream.tlj.mvp.e.DownloadTaskEntity

interface DownloadSuccessPresenter {
    val downSuccessTaskList: List<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity, deleFile: Boolean)
    fun deleTask(tasks: MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?, deleFile: Boolean)
}
