package com.dream.tlj.mvp.v

import com.dream.tlj.mvp.e.DownloadTaskEntity

interface DownLoadIngView {
    fun updateTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun startTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun stopTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun openFile(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun refreshData(tasks: List<com.dream.tlj.mvp.e.DownloadTaskEntity>)
    fun toggleDeleteButton()
    fun deleteState():Boolean
    fun alert(msg: String, msgType: Int)
    fun gotoSource(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
}
