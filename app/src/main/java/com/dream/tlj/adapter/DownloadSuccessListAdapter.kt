package com.dream.tlj.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dream.tlj.R
import com.dream.tlj.common.Const
import com.dream.tlj.mvp.e.DownloadTaskEntity
import com.dream.tlj.mvp.v.DownLoadSuccessView
import com.dream.tlj.util.FileTools
import com.dream.tlj.util.StringUtil
import com.dream.tlj.util.Util
import kotlinx.android.synthetic.main.item_download_success.view.*
import org.xutils.x
import java.io.File

class DownloadSuccessListAdapter(private val context: Context, private val downLoadSuccessView: DownLoadSuccessView, private val list: MutableList<com.dream.tlj.mvp.e.DownloadTaskEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_download_success, viewGroup, false)
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
                R.id.btn_open -> task?.let { downLoadSuccessView.openFile(it) }
                R.id.dele_task -> task?.let { downLoadSuccessView.deleTask(it) }
                R.id.btn_source -> task?.let { downLoadSuccessView.gotoSource(it) }
                R.id.file_check_box -> task?.let {
                    it.check = !it.check
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(task: com.dream.tlj.mvp.e.DownloadTaskEntity) {
            this.task = task
            val filePath = task.localPath + File.separator + task.getmFileName()
            itemView.file_name.text = task.getmFileName()

            if (StringUtil.isEmpty(task.source)) {
                itemView.btn_source.visibility = View.GONE
            } else {
                itemView.btn_source.visibility = View.VISIBLE
            }
            if (task.thumbnailPath != null && FileTools.isVideoFile(filePath)) {
                x.image().bind(itemView.file_icon, task.thumbnailPath)
            } else {
                val filename = if (task.file!!) task.getmFileName() else ""
                itemView.file_icon.setImageDrawable(itemView.resources.getDrawable(FileTools.getFileIcon(filename)))
            }
            itemView.down_size.text = FileTools.convertFileSize(task.getmDownloadSize())
            if (FileTools.exists(filePath)) {
                itemView.file_is_dele.visibility = View.GONE
                itemView.btn_open.visibility = View.VISIBLE
                itemView.file_name.setTextColor(itemView.resources.getColor(R.color.dimgray))
                itemView.down_size.setTextColor(itemView.resources.getColor(R.color.gray_8f))
                val suffix = Util.getFileSuffix(task.getmFileName()!!)
                if (FileTools.isVideoFile(task.getmFileName())) {
                    itemView.btn_open.text = itemView.resources.getString(R.string.play)
                } else if ("TORRENT" == suffix || "APK" == suffix || !task.file!! && task.taskType == Const.BT_DOWNLOAD) {
                    itemView.btn_open.text = itemView.resources.getString(R.string.open)
                } else {
                    itemView.btn_open.visibility = View.INVISIBLE
                }
            } else if (task.file!! && !FileTools.exists(filePath)) {
                itemView.file_is_dele.visibility = View.VISIBLE
                itemView.file_name.setTextColor(itemView.resources.getColor(R.color.gray_cc))
                itemView.down_size.setTextColor(itemView.resources.getColor(R.color.gray_cc))
                itemView.btn_open.text = "重新下载"
                itemView.btn_open.visibility = View.VISIBLE
            } else if (!task.file!!) {
                itemView.btn_open.visibility = View.VISIBLE
            }

            if (downLoadSuccessView.deleteState()) {
                itemView.file_check_box.visibility = View.VISIBLE
                if (task.check) {
                    itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_check))
                } else {
                    itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_uncheck))
                }
                itemView.file_check_box.setOnClickListener {

                }

            } else {
                itemView.file_check_box.visibility = View.GONE
            }

            itemView.setOnLongClickListener {
                downLoadSuccessView.toggleDeleteButton()
                notifyDataSetChanged()
                true
            }

        }

        fun onClick() {
            itemView.btn_open.setOnClickListener(onClickListener)
            itemView.dele_task.setOnClickListener(onClickListener)
            itemView.btn_source.setOnClickListener(onClickListener)
            itemView.file_check_box.setOnClickListener(onClickListener)
        }
    }
}
