package cn.sddman.download.mvp.e

import android.graphics.Bitmap

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

import java.util.Date

@Table(name = "DownloadTask")
class DownloadTaskEntity {
    @Column(name = "id", isId = true, autoGen = true)
    var id: Int = 0
    @Column(name = "taskId")
    var taskId: Long = 0
    @Column(name = "mTaskStatus")
    private var mTaskStatus: Int = 0
    @Column(name = "mFileSize")
    private var mFileSize: Long = 0
    @Column(name = "mFileName")
    private var mFileName: String? = null
    @Column(name = "taskType")
    var taskType: Int = 0
    @Column(name = "url")
    var url: String? = null
    @Column(name = "localPath")
    var localPath: String? = null
    @Column(name = "mDownloadSize")
    private var mDownloadSize: Long = 0
    @Column(name = "mDownloadSpeed")
    private var mDownloadSpeed: Long = 0
    @Column(name = "mDCDNSpeed")
    private var mDCDNSpeed: Long = 0
    @Column(name = "hash")
    var hash: String? = null
    @Column(name = "isFile")
    var file: Boolean? = null
    @Column(name = "createDate")
    var createDate: Date? = null
    @Column(name = "thumbnailPath")
    var thumbnailPath: String? = null
    var thumbnail: Bitmap? = null
    @Column(name = "isCheck")
    var check: Boolean = false
    @Column(name = "source")
    var source: String? = null
    @Column(name = "ruleId")
    var ruleId: String? = null

    fun getmTaskStatus(): Int {
        return mTaskStatus
    }

    fun setmTaskStatus(mTaskStatus: Int) {
        this.mTaskStatus = mTaskStatus
    }

    fun getmFileSize(): Long {
        return mFileSize
    }

    fun setmFileSize(mFileSize: Long) {
        this.mFileSize = mFileSize
    }

    fun getmFileName(): String? {
        return mFileName
    }

    fun setmFileName(mFileName: String) {
        this.mFileName = mFileName
    }

    fun getmDownloadSize(): Long {
        return mDownloadSize
    }

    fun setmDownloadSize(mDownloadSize: Long) {
        this.mDownloadSize = mDownloadSize
    }

    fun getmDownloadSpeed(): Long {
        return mDownloadSpeed
    }

    fun setmDownloadSpeed(mDownloadSpeed: Long) {
        this.mDownloadSpeed = mDownloadSpeed
    }

    fun getmDCDNSpeed(): Long {
        return mDCDNSpeed
    }

    fun setmDCDNSpeed(mDCDNSpeed: Long) {
        this.mDCDNSpeed = mDCDNSpeed
    }
}
