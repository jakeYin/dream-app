package com.dream.tlj.mvp.v


interface DownloadManagementView {
    fun addTaskSuccess()
    fun addTaskFail(msg: String)
    fun updataApp(version: String, url: String, content: String)
}
