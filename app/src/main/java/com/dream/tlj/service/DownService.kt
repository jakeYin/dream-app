package com.dream.tlj.service

import android.app.AlarmManager
import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder

import com.dream.tlj.thread.DownLoadingUpdateTask

class DownService : Service() {
    private val manager: AlarmManager? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        DownLoadingUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private val anHour = 1000
    }
}
