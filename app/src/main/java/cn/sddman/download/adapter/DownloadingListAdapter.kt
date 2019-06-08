package cn.sddman.download.adapter

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.coorchice.library.SuperTextView
import com.daimajia.numberprogressbar.NumberProgressBar

import java.math.BigDecimal

import cn.sddman.download.R
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.v.DownLoadIngView
import cn.sddman.download.util.FileTools
import cn.sddman.download.util.TimeUtil

class DownloadingListAdapter(private val context: Context, private val downLoadIngView: DownLoadIngView, private val list: List<DownloadTaskEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val bitmap: Bitmap? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_downloading, viewGroup, false)
        return TaskHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val task = list[i]
        val holder = viewHolder as TaskHolder
        holder.bind(task)
        holder.onClick()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal inner class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var task: DownloadTaskEntity? = null
        private val fileNameText: TextView
        private val downSize: TextView
        private val downSpeed: TextView
        private val downCDNSpeed: TextView
        private val RemainingTime: TextView
        private val downStatus: SuperTextView
        private val startTask: ImageView
        private val deleTask: ImageView
        private val fileIcon: ImageView
        private val fileIcon2: ImageView
        private val progressBar: NumberProgressBar

        private val listener = View.OnClickListener { view ->
            when (view.id) {
                R.id.start_task -> if (task!!.getmTaskStatus() == Const.DOWNLOAD_FAIL) {
                    downLoadIngView.stopTask(task!!)
                    downLoadIngView.startTask(task!!)
                } else if (task!!.getmTaskStatus() == Const.DOWNLOAD_STOP ||
                        task!!.getmTaskStatus() == Const.DOWNLOAD_CONNECTION && task!!.taskId == 0L ||
                        task!!.getmTaskStatus() == Const.DOWNLOAD_WAIT) {
                    downLoadIngView.startTask(task!!)
                } else {
                    downLoadIngView.stopTask(task!!)
                }
                R.id.dele_task -> downLoadIngView.deleTask(task!!)
                R.id.file_icon2 -> downLoadIngView.openFile(task!!)
                R.id.file_icon -> if (task!!.file!!)
                    downLoadIngView.openFile(task!!)
            }
        }

        init {
            fileNameText = itemView.findViewById<View>(R.id.file_name) as TextView
            downSize = itemView.findViewById<View>(R.id.down_size) as TextView
            downSpeed = itemView.findViewById<View>(R.id.down_speed) as TextView
            downCDNSpeed = itemView.findViewById<View>(R.id.down_cdnspeed) as TextView
            RemainingTime = itemView.findViewById<View>(R.id.remaining_time) as TextView
            startTask = itemView.findViewById<View>(R.id.start_task) as ImageView
            deleTask = itemView.findViewById<View>(R.id.dele_task) as ImageView
            fileIcon = itemView.findViewById<View>(R.id.file_icon) as ImageView
            fileIcon2 = itemView.findViewById<View>(R.id.file_icon2) as ImageView
            progressBar = itemView.findViewById<View>(R.id.number_progress_bar) as NumberProgressBar
            downStatus = itemView.findViewById<View>(R.id.down_status) as SuperTextView
        }

        fun bind(task: DownloadTaskEntity) {
            this.task = task
            fileNameText.text = task.getmFileName()
            if (task.file!!) {
                fileIcon.setImageDrawable(ContextCompat.getDrawable(context, FileTools.getFileIcon(task.getmFileName())))
            } else {
                fileIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_floder))
            }
//            if (task.getThumbnail() != null) {
//                fileIcon.setImageBitmap(task.getThumbnail());
//            }
            downSize.text = String.format(itemView.resources.getString(R.string.down_count),
                    FileTools.convertFileSize(task.getmDownloadSize()), FileTools.convertFileSize(task.getmFileSize()))
            downSpeed.text = String.format(itemView.resources.getString(R.string.down_speed),
                    FileTools.convertFileSize(task.getmDownloadSpeed()))
            downCDNSpeed.text = String.format(itemView.resources.getString(R.string.down_speed_up),
                    FileTools.convertFileSize(task.getmDCDNSpeed()))
            if (task.getmFileSize() != 0L && task.getmDownloadSize() != 0L) {
                val speed = if (task.getmDownloadSpeed() == 0L) 1 else task.getmDownloadSpeed()
                val time = (task.getmFileSize() - task.getmDownloadSize()) / speed
                RemainingTime.text = String.format(itemView.resources.getString(R.string.remaining_time), TimeUtil.formatFromSecond(time.toInt()))
            }
            if (task.thumbnailPath != null) {
                fileIcon2.visibility = View.VISIBLE
            } else {
                fileIcon2.visibility = View.GONE
            }

            //            String videoPath=task.getLocalPath()+ File.separator+task.getmFileName();
            //            bitmap = FileTools.getVideoThumbnail(videoPath, 200, 200, MediaStore.Video.Thumbnails.MICRO_KIND);
            //           if (bitmap != null) {
            //                task.setThumbnail(bitmap);
            //               fileIcon2.setVisibility(View.VISIBLE);
            //            }

            if (task.getmDownloadSize() != 0L && task.getmFileSize() != 0L) {
                val f1 = BigDecimal((task.getmDownloadSize().toFloat() / task.getmFileSize()).toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                progressBar.progress = (f1 * 100).toInt()
                if (FileTools.isVideoFile(task.getmFileName()) && f1 * 100 > 1) {
                    //fileIcon2.setVisibility(View.VISIBLE);
                }

            } else {
                progressBar.progress = 0
            }
            //if(SystemConfig.getNetType())
            if (task.getmTaskStatus() == Const.DOWNLOAD_STOP || task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION && task.taskId == 0L) {
                startTask.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_download))
                downStatus.setText(R.string.is_stop)
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION) {
                startTask.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_connent))
                downStatus.text = "连接中"
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_FAIL) {
                startTask.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fail))
                downStatus.text = "下载失败"
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_WAIT) {
                startTask.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_wait))
                downStatus.setText(R.string.wait_down)
            } else {
                startTask.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause))
                downStatus.setText(R.string.downloading)
            }
        }

        fun onClick() {
            startTask.setOnClickListener(listener)
            deleTask.setOnClickListener(listener)
            fileIcon2.setOnClickListener(listener)
            fileIcon.setOnClickListener(listener)
        }
    }
}
