package cn.sddman.download.thread

import android.os.AsyncTask
import android.os.SystemClock

import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.util.DownUtil

class DownLoadingTask : AsyncTask<Void, List<DownloadTaskEntity>, List<DownloadTaskEntity>>() {
    override fun doInBackground(vararg objects: Void): List<DownloadTaskEntity>? {
        while (DownUtil.instance.isIsLoopDown) {
            DownUpdateUI.instance.downUpdate()
            SystemClock.sleep(2000)
        }
        return null
    }

    override fun onProgressUpdate(vararg values: List<DownloadTaskEntity>) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(downloadTaskEntities: List<DownloadTaskEntity>) {
        super.onPostExecute(downloadTaskEntities)
    }
}
