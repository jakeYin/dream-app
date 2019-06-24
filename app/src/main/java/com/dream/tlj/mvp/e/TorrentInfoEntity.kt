package com.dream.tlj.mvp.e

import android.graphics.Bitmap

class TorrentInfoEntity {
    private var mFileIndex: Int = 0
    private var mFileName: String? = null
    private var mFileSize: Long = 0
    private var mRealIndex: Int = 0
    var path: String? = null
    private var mSubPath: String? = null
    var playUrl: String? = null
    var hash: String? = null
    var thumbnail: Bitmap? = null
    var check: Boolean? = false

    fun getmFileIndex(): Int {
        return mFileIndex
    }

    fun setmFileIndex(mFileIndex: Int) {
        this.mFileIndex = mFileIndex
    }

    fun getmFileName(): String? {
        return mFileName
    }

    fun setmFileName(mFileName: String) {
        this.mFileName = mFileName
    }

    fun getmFileSize(): Long {
        return mFileSize
    }

    fun setmFileSize(mFileSize: Long) {
        this.mFileSize = mFileSize
    }

    fun getmRealIndex(): Int {
        return mRealIndex
    }

    fun setmRealIndex(mRealIndex: Int) {
        this.mRealIndex = mRealIndex
    }

    fun getmSubPath(): String? {
        return mSubPath
    }

    fun setmSubPath(mSubPath: String) {
        this.mSubPath = mSubPath
    }
}
