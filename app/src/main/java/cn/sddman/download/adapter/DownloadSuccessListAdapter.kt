package cn.sddman.download.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.sddman.download.R
import cn.sddman.download.common.Const
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.v.DownLoadSuccessView
import cn.sddman.download.util.FileTools
import cn.sddman.download.util.StringUtil
import cn.sddman.download.util.Util
import com.coorchice.library.SuperTextView
import org.xutils.x
import java.io.File

class DownloadSuccessListAdapter(private val context: Context, private val downLoadSuccessView: DownLoadSuccessView, private val list: MutableList<DownloadTaskEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        private var task: DownloadTaskEntity? = null
        private val fileNameText: TextView = itemView.findViewById<View>(R.id.file_name) as TextView
        private val downSize: TextView = itemView.findViewById<View>(R.id.down_size) as TextView
        private val fileIcon: ImageView = itemView.findViewById<View>(R.id.file_icon) as ImageView
        private val fileCheckBox: ImageView = itemView.findViewById<View>(R.id.file_check_box) as ImageView
        private val deleTask: ImageView = itemView.findViewById<View>(R.id.dele_task) as ImageView
        private val btnOpen: SuperTextView = itemView.findViewById<View>(R.id.btn_open) as SuperTextView
        private val btnSource: SuperTextView = itemView.findViewById<View>(R.id.btn_source) as SuperTextView
        private val fileIsDele: SuperTextView = itemView.findViewById<View>(R.id.file_is_dele) as SuperTextView

        private val listener = View.OnClickListener { view ->
            when (view.id) {
                R.id.btn_open -> task?.let { downLoadSuccessView.openFile(it) }
                R.id.dele_task -> task?.let { downLoadSuccessView.deleTask(it) }
                R.id.btn_source -> task?.let { downLoadSuccessView.gotoSource(it) }
            }
        }


        fun bind(task: DownloadTaskEntity) {
            this.task = task
            val filePath = task.localPath + File.separator + task.getmFileName()
            fileNameText.text = task.getmFileName()

            if (StringUtil.isEmpty(task.source)){
                btnSource.visibility = View.GONE
            } else {
                btnSource.visibility = View.VISIBLE
            }
            if (task.thumbnailPath != null && FileTools.isVideoFile(filePath)) {
                x.image().bind(fileIcon, task.thumbnailPath)
            } else {
                val filename = if (task.file!!) task.getmFileName() else ""
                fileIcon.setImageDrawable(itemView.resources.getDrawable(FileTools.getFileIcon(filename)))
            }
            downSize.text = FileTools.convertFileSize(task.getmDownloadSize())
            if (FileTools.exists(filePath)) {
                fileIsDele.visibility = View.GONE
                btnOpen.visibility = View.VISIBLE
                fileNameText.setTextColor(itemView.resources.getColor(R.color.dimgray))
                downSize.setTextColor(itemView.resources.getColor(R.color.gray_8f))
                val suffix = Util.getFileSuffix(task.getmFileName()!!)
                if (FileTools.isVideoFile(task.getmFileName())) {
                    btnOpen.text = itemView.resources.getString(R.string.play)
                } else if ("TORRENT" == suffix || "APK" == suffix || !task.file!! && task.taskType == Const.BT_DOWNLOAD) {
                    btnOpen.text = itemView.resources.getString(R.string.open)
                } else {
                    btnOpen.visibility = View.INVISIBLE
                }
            } else if (task.file!! && !FileTools.exists(filePath)) {
                fileIsDele.visibility = View.VISIBLE
                fileNameText.setTextColor(itemView.resources.getColor(R.color.gray_cc))
                downSize.setTextColor(itemView.resources.getColor(R.color.gray_cc))
                btnOpen.text = "重新下载"
                btnOpen.visibility = View.VISIBLE
            } else if (!task.file!!) {
                btnOpen.visibility = View.VISIBLE
            }

            if (downLoadSuccessView.deleteState()){
                fileCheckBox.visibility = View.VISIBLE
                if (task.check) {
                    fileCheckBox.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_check))
                } else {
                    fileCheckBox.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_uncheck))
                }
                fileCheckBox.setOnClickListener {
                    task.check = !task.check
                    notifyDataSetChanged()
                }

            } else {
                fileCheckBox.visibility = View.GONE
            }

            itemView.setOnLongClickListener {
                downLoadSuccessView.toggleDeleteButton()
                notifyDataSetChanged()
                true
            }

        }

        fun onClick() {
            btnOpen.setOnClickListener(listener)
            deleTask.setOnClickListener(listener)
            btnSource.setOnClickListener(listener)
        }
    }
}
