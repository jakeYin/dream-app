package com.dream.tlj.adapter

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dream.tlj.R
import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.mvp.v.DownLoadIngView
import com.dream.tlj.util.FileTools
import com.dream.tlj.util.StringUtil
import com.dream.tlj.util.TimeUtil
import kotlinx.android.synthetic.main.item_downloading.view.*
import java.math.BigDecimal

class DownloadingListAdapter(private val context: Context, private val downLoadIngView: DownLoadIngView, private val list: List<com.dream.tlj.mvp.e.DownloadTaskEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        private var task: com.dream.tlj.mvp.e.DownloadTaskEntity? = null
        private val onClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.start_task -> if (task!!.getmTaskStatus() == Const.DOWNLOAD_FAIL) {
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
                R.id.file_icon -> if (task!!.file!!) {
                    downLoadIngView.openFile(task!!)
                }
                R.id.btn_source -> task?.let { downLoadIngView.gotoSource(it) }
                R.id.file_check_box->task?.let {
                    it.check = !it.check
                    notifyDataSetChanged()
                    downLoadIngView.updateTask(it)
                }
            }
        }

        fun bind(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
            this.task = task
            itemView.file_name.text = task.getmFileName()
            if (task.file!!) {
                itemView.file_icon.setImageDrawable(ContextCompat.getDrawable(context, FileTools.getFileIcon(task.getmFileName())))
            } else {
                itemView.file_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_floder))
            }

            if (StringUtil.isEmpty(task.source)){
                itemView.btn_source.visibility = View.GONE
            } else {
                itemView.btn_source.visibility = View.VISIBLE
            }
//            if (task.getThumbnail() != null) {
//                fileIcon.setImageBitmap(task.getThumbnail());
//            }
            itemView.down_size.text = String.format(itemView.resources.getString(R.string.down_count),
                    FileTools.convertFileSize(task.getmDownloadSize()), FileTools.convertFileSize(task.getmFileSize()))
            itemView.down_speed.text = String.format(itemView.resources.getString(R.string.down_speed),
                    FileTools.convertFileSize(task.getmDownloadSpeed()))
            itemView.down_cdnspeed.text = String.format(itemView.resources.getString(R.string.down_speed_up),
                    FileTools.convertFileSize(task.getmDCDNSpeed()))
            if (task.getmFileSize() != 0L && task.getmDownloadSize() != 0L) {
                val speed = if (task.getmDownloadSpeed() == 0L) 1 else task.getmDownloadSpeed()
                val time = (task.getmFileSize() - task.getmDownloadSize()) / speed
                itemView.remaining_time.text = String.format(itemView.resources.getString(R.string.remaining_time), TimeUtil.formatFromSecond(time.toInt()))
            }
            if (task.thumbnailPath != null) {
                itemView.file_icon2.visibility = View.VISIBLE
            } else {
                itemView.file_icon2.visibility = View.GONE
            }

            //            String videoPath=task.getLocalPath()+ File.separator+task.getmFileName();
            //            bitmap = FileTools.getVideoThumbnail(videoPath, 200, 200, MediaStore.Video.Thumbnails.MICRO_KIND);
            //           if (bitmap != null) {
            //                task.setThumbnail(bitmap);
            //               fileIcon2.setVisibility(View.VISIBLE);
            //            }

            if (task.getmDownloadSize() != 0L && task.getmFileSize() != 0L) {
                val f1 = BigDecimal((task.getmDownloadSize().toFloat() / task.getmFileSize()).toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                itemView.number_progress_bar.progress = (f1 * 100).toInt()
                if (FileTools.isVideoFile(task.getmFileName()) && f1 * 100 > 1) {
                    //fileIcon2.setVisibility(View.VISIBLE);
                }

            } else {
                itemView.number_progress_bar.progress = 0
            }
            if (task.getmTaskStatus() == Const.DOWNLOAD_STOP || task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION && task.taskId == 0L) {
                itemView.start_task.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_download))
                itemView.down_status.setText(R.string.is_stop)
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION) {
                itemView.start_task.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_connent))
                itemView.down_status.text = "连接中"
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_FAIL) {
                itemView.start_task.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fail))
                itemView.down_status.text = "下载失败"
            } else if (task.getmTaskStatus() == Const.DOWNLOAD_WAIT) {
                itemView.start_task.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_wait))
                itemView.down_status.setText(R.string.wait_down)
            } else {
                itemView.start_task.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause))
                itemView.down_status.setText(R.string.downloading)
            }

            if (downLoadIngView.deleteState()){
                itemView.file_check_box.visibility = View.VISIBLE
                if (task.check) {
                    itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_check))
                } else {
                    itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_uncheck))
                }
            } else {
                itemView.file_check_box.visibility = View.GONE
            }

            itemView.setOnLongClickListener {
                downLoadIngView.toggleDeleteButton()
                notifyDataSetChanged()
                true
            }
        }

        fun onClick() {
            itemView.start_task.setOnClickListener(onClickListener)
            itemView.dele_task.setOnClickListener(onClickListener)
            itemView.file_icon2.setOnClickListener(onClickListener)
            itemView.file_icon.setOnClickListener(onClickListener)
            itemView.file_check_box.setOnClickListener(onClickListener)
            itemView.btn_source.setOnClickListener(onClickListener)
        }
    }
}
