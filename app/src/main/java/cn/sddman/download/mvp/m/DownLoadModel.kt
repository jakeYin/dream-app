package cn.sddman.download.mvp.m

import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.e.TorrentInfoEntity

interface DownLoadModel {
    fun startTorrentTask(bt: DownloadTaskEntity): Boolean
    fun startTorrentTask(btpath: String): Boolean
    fun startTorrentTask(bt: DownloadTaskEntity, indexs: IntArray): Boolean
    fun startUrlTask(url: String,source:String?,ruleId:String): Boolean
    fun startUrlTask(url: String): Boolean
    fun startTorrentTask(btpath: String, indexs: IntArray?): Boolean
    fun startTask(task: DownloadTaskEntity): Boolean
    fun stopTask(task: DownloadTaskEntity): Boolean
    fun getLoclUrl(task: DownloadTaskEntity): String
    fun deleTask(task: DownloadTaskEntity, deleFile: Boolean): Boolean
    fun deleTask(task: DownloadTaskEntity, stopTask: Boolean, deleFile: Boolean): Boolean
    fun getTorrentInfo(bt: DownloadTaskEntity): List<TorrentInfoEntity>
    fun getTorrentInfo(btpath: String): List<TorrentInfoEntity>
}
