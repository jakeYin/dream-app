package cn.sddman.download.mvp.m

import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.e.TorrentInfoEntity
import cn.sddman.download.util.AppSettingUtil
import cn.sddman.download.util.DBTools
import cn.sddman.download.util.FileTools
import com.xunlei.downloadlib.XLTaskHelper
import org.xutils.ex.DbException
import org.xutils.x
import java.io.File
import java.util.*

class DownLoadModelImp : DownLoadModel {
    override fun startTorrentTask(bt: DownloadTaskEntity): Boolean {
        val path = bt.url as String
        try {
            DBTools.instance.db().delete(bt)
        } catch (e: DbException) {
            e.printStackTrace()
        }

        return startTorrentTask(path, null)
    }

    override fun startTorrentTask(btpath: String): Boolean {
        return startTorrentTask(btpath, null)
    }

    override fun startTorrentTask(bt: DownloadTaskEntity, indexs: IntArray): Boolean {
        val path = bt.localPath + File.separator + bt.getmFileName()
        return startTorrentTask(path, indexs)
    }

    override fun getLoclUrl(task: DownloadTaskEntity): String {
        return XLTaskHelper.instance(x.app().applicationContext).getLoclUrl(task.localPath + "/" + task.getmFileName());
    }

    override fun startUrlTask(url: String): Boolean {
        val task = DownloadTaskEntity()
        task.taskType = Const.URL_DOWNLOAD
        task.url = url
        task.localPath = AppSettingUtil.instance.fileSavePath
        try {
            val taskId = XLTaskHelper.instance(x.app().applicationContext).addThunderTask(url, AppSettingUtil.instance.fileSavePath, null)
            val taskInfo = XLTaskHelper.instance(x.app().applicationContext).getTaskInfo(taskId)
            task.setmFileName(XLTaskHelper.instance(x.app().applicationContext).getFileName(url))
            task.setmFileSize(taskInfo.mFileSize)
            task.setmTaskStatus(taskInfo.mTaskStatus)
            task.taskId = taskId
            task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed)
            task.setmDownloadSize(taskInfo.mDownloadSize)
            task.setmDownloadSpeed(taskInfo.mDownloadSpeed)
            task.file = true
            task.createDate = Date()
            DBTools.instance.db().saveBindingId(task)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun startUrlTask(url: String, source: String?, ruleId: String): Boolean {
        val task = DownloadTaskEntity()
        task.taskType = Const.URL_DOWNLOAD
        task.url = url
        source?.let {
            task.source = it
            task.ruleId = ruleId
        }
        task.localPath = AppSettingUtil.instance.fileSavePath
        try {
            val taskId = XLTaskHelper.instance(x.app().applicationContext).addThunderTask(url, AppSettingUtil.instance.fileSavePath, null)
            val taskInfo = XLTaskHelper.instance(x.app().applicationContext).getTaskInfo(taskId)
            task.setmFileName(XLTaskHelper.instance(x.app().applicationContext).getFileName(url))
            task.setmFileSize(taskInfo.mFileSize)
            task.setmTaskStatus(taskInfo.mTaskStatus)
            task.taskId = taskId
            task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed)
            task.setmDownloadSize(taskInfo.mDownloadSize)
            task.setmDownloadSpeed(taskInfo.mDownloadSpeed)
            task.file = true
            task.createDate = Date()
            DBTools.instance.db().saveBindingId(task)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun startTorrentTask(btpath: String, indexs: IntArray?): Boolean {
        var indexs = indexs
        val task = DownloadTaskEntity()
        val torrentInfo = XLTaskHelper.instance(x.app().applicationContext).getTorrentInfo(btpath)
        if (indexs == null || indexs.size <= 0) {
            var i = 0
            indexs = IntArray(torrentInfo.mSubFileInfo.size)
            for (torrent in torrentInfo.mSubFileInfo) {
                indexs[i++] = torrent.mFileIndex
            }
        }
        var savePath = AppSettingUtil.instance.fileSavePath
        if (torrentInfo.mIsMultiFiles) {
            savePath += File.separator + torrentInfo.mMultiFileBaseFolder
            task.setmFileName(torrentInfo.mMultiFileBaseFolder)
        } else {
            if (torrentInfo.mSubFileInfo.size > 1) {
                savePath += File.separator + FileTools.getFileNameWithoutSuffix(btpath!!)
                task.setmFileName(FileTools.getFileNameWithoutSuffix(btpath))
            } else {
                task.setmFileName(torrentInfo.mSubFileInfo[0].mFileName)
            }
        }
        var taskId: Long
        try {
            taskId = XLTaskHelper.instance(x.app().applicationContext).addTorrentTask(btpath, savePath, indexs)
            val taskInfo = XLTaskHelper.instance(x.app().applicationContext).getTaskInfo(taskId)
            task.localPath = savePath
            task.file = !torrentInfo.mIsMultiFiles
            task.hash = torrentInfo.mInfoHash
            task.url = btpath
            task.setmFileSize(taskInfo.mFileSize)
            task.setmTaskStatus(taskInfo.mTaskStatus)
            task.taskId = taskId
            task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed)
            task.setmDownloadSize(taskInfo.mDownloadSize)
            task.setmDownloadSpeed(taskInfo.mDownloadSpeed)
            task.taskType = Const.BT_DOWNLOAD
            task.createDate = Date()
            DBTools.instance.db().saveBindingId(task)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun startTask(task: DownloadTaskEntity): Boolean {
        try {
            var taskId: Long = 0
            if (task.taskType == Const.BT_DOWNLOAD) {
                val torrentInfo = XLTaskHelper.instance(x.app().applicationContext).getTorrentInfo(task.url)
                var i = 0
                val indexs = IntArray(torrentInfo.mSubFileInfo.size)
                for (torrent in torrentInfo.mSubFileInfo) {
                    indexs[i++] = torrent.mFileIndex
                }
                taskId = XLTaskHelper.instance(x.app().applicationContext).addTorrentTask(task.url, task.localPath, indexs)
            } else if (task.taskType == Const.URL_DOWNLOAD) {
                taskId = XLTaskHelper.instance(x.app().applicationContext).addThunderTask(task.url, task.localPath, null)
            }
            val taskInfo = XLTaskHelper.instance(x.app().applicationContext).getTaskInfo(taskId)
            task.setmFileSize(taskInfo.mFileSize)
            task.taskId = taskId
            task.setmTaskStatus(taskInfo.mTaskStatus)
            DBTools.instance.db().saveOrUpdate(task)
            if (taskInfo.mTaskId == 0L)
                return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun stopTask(task: DownloadTaskEntity): Boolean {
        try {
            XLTaskHelper.instance(x.app().applicationContext).stopTask(task.taskId)
            task.setmTaskStatus(Const.DOWNLOAD_STOP)
            task.setmDownloadSpeed(0)
            task.setmDCDNSpeed(0)
            DBTools.instance.db().saveOrUpdate(task)
        } catch (e: DbException) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun deleTask(task: DownloadTaskEntity, deleFile: Boolean): Boolean {
        try {
            DBTools.instance.db().delete(task)
            if (deleFile!!) {
                if (task.file!!) {
                    FileTools.deleteFile(task.localPath + File.separator + task.getmFileName())
                } else {
                    FileTools.deleteDir(task.localPath!!)
                }
            }
        } catch (e: DbException) {
            e.printStackTrace()
            return false
        }

        return true
    }

    override fun deleTask(task: DownloadTaskEntity, stopTask: Boolean, deleFile: Boolean): Boolean {
        if (stopTask) {
            XLTaskHelper.instance(x.app().applicationContext).stopTask(task.taskId)
        }
        return deleTask(task, deleFile)
    }

    override fun getTorrentInfo(bt: DownloadTaskEntity): List<TorrentInfoEntity> {
        val path = bt.localPath + File.separator + bt.getmFileName()
        return getTorrentInfo(path)
    }

    override fun getTorrentInfo(btpath: String): List<TorrentInfoEntity> {
        val torrentInfo = XLTaskHelper.instance(x.app().applicationContext).getTorrentInfo(btpath)
        val list = ArrayList<TorrentInfoEntity>()
        for (torrent in torrentInfo.mSubFileInfo) {
            val tie = TorrentInfoEntity()
            tie.hash = torrent.hash
            tie.setmFileIndex(torrent.mFileIndex)
            tie.setmFileName(torrent.mFileName)
            tie.setmFileSize(torrent.mFileSize)
            tie.setmSubPath(torrent.mSubPath)
            tie.setmRealIndex(torrent.mRealIndex)
            tie.path = AppSettingUtil.instance.fileSavePath +
                    File.separator + torrentInfo.mMultiFileBaseFolder +
                    File.separator + torrent.mSubPath + File.separator + torrent.mFileName
            list.add(tie)
        }
        return list
    }
}
