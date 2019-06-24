package com.dream.tlj.mvp.v

import com.dream.tlj.mvp.e.DownloadTaskEntity

interface DownLoadSuccessView {
    fun initTaskListView(list: MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>)
    fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun openFile(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
    fun refreshData()
    fun alert(msg: String, msgType: Int)
    fun toggleDeleteButton()
    fun deleteState():Boolean
    fun gotoSource(task: com.dream.tlj.mvp.e.DownloadTaskEntity)
}
