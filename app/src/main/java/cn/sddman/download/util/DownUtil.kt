package cn.sddman.download.util

import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity

class DownUtil {

    var isIsLoopDown: Boolean
        get() = isLoopDown
        set(isLoopDown) {
            DownUtil.isLoopDown = isLoopDown
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

        fun isDownSuccess(task: DownloadTaskEntity): Boolean {
            val fileSize = if (task.file!!) task.getmFileSize() else task.getmFileSize() - 10000
            return if (task.getmTaskStatus() == Const.DOWNLOAD_SUCCESS || task.getmFileSize() > 0 && task.getmDownloadSize() > 0 && fileSize <= task.getmDownloadSize()) {
                true
            } else false
        }
    }
}
