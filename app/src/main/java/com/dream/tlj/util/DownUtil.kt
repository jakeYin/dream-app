package com.dream.tlj.util

import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.DownloadTaskEntity

class DownUtil {

    var isIsLoopDown: Boolean
        get() = isLoopDown
        set(isLoopDown) {
            Companion.isLoopDown = isLoopDown
        }

    companion object {
        private var downUtil: DownUtil? = null
        private var isLoopDown = true
        val instance: DownUtil
            @Synchronized get() {
                if (downUtil == null) {
                    downUtil = DownUtil()
                }
                return downUtil!!
            }

        fun isDownSuccess(task: com.dream.tlj.mvp.e.DownloadTaskEntity): Boolean {
            val fileSize = if (task.file!!) task.getmFileSize() else task.getmFileSize() - 10000
            return if (task.getmTaskStatus() == Const.DOWNLOAD_SUCCESS || task.getmFileSize() > 0 && task.getmDownloadSize() > 0 && fileSize <= task.getmDownloadSize()) {
                true
            } else false
        }
    }
}
