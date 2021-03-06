package com.dream.tlj.fragment

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.dream.tlj.R
import com.dream.tlj.activity.DownloadManagementActivity
import com.dream.tlj.common.AppManager
import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.util.FileTools
import com.dream.tlj.util.TimeUtil
import org.xutils.x
import java.math.BigDecimal
import java.util.*

class DownProgressNotify {
    private val notificationManager: NotificationManager
    private val downRemoteViews: MutableMap<String, RemoteViews>
    private val downNotification: MutableMap<String, Notification>
    private val context: Context? = null
    private var mainPendingIntent: PendingIntent? = null

    init {
        //this.context=context;
        notificationManager = x.app().getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(x.app().packageName, x.app().getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
            channel.enableLights(false)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }
        downRemoteViews = HashMap()
        downNotification = HashMap()
    }

    fun createDowneProgressNotify(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        var notification  = downNotification.get(task.id.toString())
        val mRemoteViews: RemoteViews
        if (notification != null) {
            return
        } else {
            var progress = 0
            if (task.getmDownloadSize() != 0L && task.getmFileSize() != 0L) {
                val f1 = BigDecimal((task.getmDownloadSize().toFloat() / task.getmFileSize()).toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                progress = (f1 * 100).toInt()
            }
            mRemoteViews = RemoteViews(x.app().packageName, R.layout.item_down_progress_notify)
            val fileName = if (task.file!!) task.getmFileName() else ""
            mRemoteViews.setImageViewResource(R.id.file_icon, FileTools.getFileIcon(fileName))
            mRemoteViews.setTextViewText(R.id.file_name, task.getmFileName())
            mRemoteViews.setTextViewText(R.id.down_size, String.format(x.app().resources.getString(R.string.down_count),
                    FileTools.convertFileSize(task.getmFileSize()), FileTools.convertFileSize(task.getmDownloadSize())))
            mRemoteViews.setTextViewText(R.id.down_speed, String.format(x.app().resources.getString(R.string.down_speed),
                    FileTools.convertFileSize(task.getmDownloadSpeed())))
            mRemoteViews.setTextViewText(R.id.down_cdnspeed, String.format(x.app().resources.getString(R.string.down_speed_up),
                    FileTools.convertFileSize(task.getmDCDNSpeed())))
            if (task.getmFileSize() != 0L && task.getmDownloadSize() != 0L) {
                val speed = if (task.getmDownloadSpeed() == 0L) 1 else task.getmDownloadSpeed()
                val time = (task.getmFileSize() - task.getmDownloadSize()) / speed
                mRemoteViews.setTextViewText(R.id.remaining_time, String.format(x.app().getString(R.string.remaining_time), TimeUtil.formatFromSecond(time.toInt())))
            }
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, progress, false)
            downRemoteViews[task.id.toString() + ""] = mRemoteViews
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = Notification.Builder(x.app().applicationContext, x.app().packageName)
                    .setContentTitle("下载进度:" + task.getmFileName()!!).setSmallIcon(R.drawable.logo).setCustomContentView(mRemoteViews)
                    .build()
        } else {
            notification = Notification.Builder(x.app().applicationContext)
                    .setContentTitle("下载进度:" + task.getmFileName()!!).setSmallIcon(R.drawable.logo).setContent(mRemoteViews)
                    .build()
        }
        val mainIntent = Intent(AppManager.appManager.currentActivity(), DownloadManagementActivity::class.java)
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        mainPendingIntent = PendingIntent.getActivity(AppManager.appManager.currentActivity(), 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notification!!.contentIntent = mainPendingIntent
        downNotification[task.id.toString() + ""] = notification
        notificationManager.notify(task.id, notification)
    }

    fun updateDownProgressNotify(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        val notification = downNotification[task.id.toString() + ""]
        var progress = 0
        if (task.getmDownloadSize() != 0L && task.getmFileSize() != 0L) {
            val f1 = BigDecimal((task.getmDownloadSize().toFloat() / task.getmFileSize()).toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
            progress = (f1 * 100).toInt()
        }
        notification?.contentView?.setTextViewText(R.id.down_size, String.format(x.app().resources.getString(R.string.down_count),
                FileTools.convertFileSize(task.getmFileSize()), FileTools.convertFileSize(task.getmDownloadSize())))
        notification?.contentView?.setTextViewText(R.id.down_speed, String.format(x.app().resources.getString(R.string.down_speed),
                FileTools.convertFileSize(task.getmDownloadSpeed())))
        notification?.contentView?.setTextViewText(R.id.down_cdnspeed, String.format(x.app().resources.getString(R.string.down_speed_up),
                FileTools.convertFileSize(task.getmDCDNSpeed())))
        if (task.getmFileSize() != 0L && task.getmDownloadSize() != 0L) {
            val speed = if (task.getmDownloadSpeed() == 0L) 1 else task.getmDownloadSpeed()
            val time = (task.getmFileSize() - task.getmDownloadSize()) / speed
            notification?.contentView?.setTextViewText(R.id.remaining_time, String.format(x.app().getString(R.string.remaining_time), TimeUtil.formatFromSecond(time.toInt())))
        }
        notification?.contentView?.setProgressBar(R.id.custom_progressbar, 100, progress, false)
        notificationManager.notify(task.id, notification)
        if (progress >= 100) {
            notificationManager.cancel(task.id)
        }
    }

    fun cancelDownProgressNotify(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
        notificationManager.cancel(task.id)
//        val notification = downNotification[task.id.toString() + ""] as String?
        downNotification.remove(task.id.toString() + "")
    }

    companion object {
        private var progressNotify: DownProgressNotify? = null
        val instance: DownProgressNotify
            @Synchronized get() {
                if (progressNotify == null) {
                    progressNotify = DownProgressNotify()
                }
                return progressNotify!!
            }
    }
}
