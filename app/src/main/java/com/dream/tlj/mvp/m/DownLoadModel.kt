package com.dream.tlj.mvp.m

import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.mvp.e.TorrentInfoEntity

interface DownLoadModel {
    fun startTorrentTask(bt: com.dream.tlj.mvp.e.DownloadTaskEntity): Boolean
    fun startTorrentTask(btpath: String): Boolean
    fun startTorrentTask(bt: com.dream.tlj.mvp.e.DownloadTaskEntity, indexs: IntArray): Boolean
    fun startUrlTask(url: String,source:String?,ruleId:String): Boolean
    fun startUrlTask(url: String): Boolean
    fun startTorrentTask(btpath: String, indexs: IntArray?): Boolean
    fun startTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity): Boolean
    fun stopTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity): Boolean
    fun getLoclUrl(task: com.dream.tlj.mvp.e.DownloadTaskEntity): String
    fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity, deleFile: Boolean): Boolean
    fun deleTask(task: com.dream.tlj.mvp.e.DownloadTaskEntity, stopTask: Boolean, deleFile: Boolean): Boolean
    fun getTorrentInfo(bt: com.dream.tlj.mvp.e.DownloadTaskEntity): List<TorrentInfoEntity>
    fun getTorrentInfo(btpath: String): List<TorrentInfoEntity>
}
