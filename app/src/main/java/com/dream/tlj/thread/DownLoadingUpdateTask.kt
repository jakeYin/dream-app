package com.dream.tlj.thread

import android.os.AsyncTask
import android.os.SystemClock

import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.util.DownUtil

class DownLoadingUpdateTask : AsyncTask<Void, List<com.dream.tlj.mvp.e.DownloadTaskEntity>, List<com.dream.tlj.mvp.e.DownloadTaskEntity>>() {
    override fun doInBackground(vararg objects: Void): List<com.dream.tlj.mvp.e.DownloadTaskEntity>? {
        while (DownUtil.instance.isIsLoopDown) {
            DownUpdateUI.instance.downUpdate()
            SystemClock.sleep(1000)
        }
        return null
    }

    override fun onProgressUpdate(vararg values: List<com.dream.tlj.mvp.e.DownloadTaskEntity>) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(downloadTaskEntities: List<com.dream.tlj.mvp.e.DownloadTaskEntity>) {
        super.onPostExecute(downloadTaskEntities)
    }
}
