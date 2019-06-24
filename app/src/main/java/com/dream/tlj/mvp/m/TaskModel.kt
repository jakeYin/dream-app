package com.dream.tlj.mvp.m

import com.dream.tlj.mvp.e.DownloadTaskEntity

interface TaskModel {
    fun findAllTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun findTaskByUrl(url: String): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun findTaskByHash(hash: String): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun findTaskById(id: Int): com.dream.tlj.mvp.e.DownloadTaskEntity?
    fun findLoadingTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun findDowningTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun findSuccessTask(): MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>?
    fun updateTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity): com.dream.tlj.mvp.e.DownloadTaskEntity?
    fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity): Boolean
}
