package cn.sddman.download.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.coorchice.library.SuperTextView

import cn.sddman.download.R
import cn.sddman.download.mvp.e.DownloadTaskEntity
import cn.sddman.download.mvp.e.TorrentInfoEntity
import cn.sddman.download.mvp.v.DownLoadSuccessView
import cn.sddman.download.mvp.v.TorrentInfoView
import cn.sddman.download.util.FileTools

class TorrentInfoAdapter(private val context: Context, private val torrentInfoView: TorrentInfoView, private val list: List<TorrentInfoEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_torrent_info, viewGroup, false)
        return TaskHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val task = list[i]
        viewHolder.itemView.setOnClickListener { torrentInfoView.itemClick(i) }
        val holder = viewHolder as TaskHolder
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal inner class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var task: TorrentInfoEntity? = null
        private val fileNameText: TextView
        private val fileIcon: ImageView
        private val fileCheckBox: ImageView
        private val fileType: SuperTextView
        private val fileSize: SuperTextView
        private val filePlayer: SuperTextView

        init {
            fileNameText = itemView.findViewById<View>(R.id.file_name) as TextView
            fileIcon = itemView.findViewById<View>(R.id.file_icon) as ImageView
            fileCheckBox = itemView.findViewById<View>(R.id.file_check_box) as ImageView
            fileSize = itemView.findViewById<View>(R.id.file_size) as SuperTextView
            fileType = itemView.findViewById<View>(R.id.file_type) as SuperTextView
            filePlayer = itemView.findViewById<View>(R.id.file_play) as SuperTextView
        }

        fun bind(task: TorrentInfoEntity) {
            this.task = task
            val suffix = task.getmFileName()!!.substring(task.getmFileName()!!.lastIndexOf(".") + 1)
            fileNameText.text = task.getmFileName()

            fileSize.text = FileTools.convertFileSize(task.getmFileSize())
            fileType.setText(suffix)
            if (torrentInfoView.isDown) {
                fileIcon.setImageDrawable(itemView.resources.getDrawable(FileTools.getFileIcon(task.getmFileName())))
                if (task.check!!) {
                    fileCheckBox.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_check))
                } else {
                    fileCheckBox.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_uncheck))
                }
            } else {
                fileCheckBox.visibility = View.GONE
                fileIcon.setImageDrawable(itemView.resources.getDrawable(FileTools.getFileIcon(task.getmFileName())))
                if (FileTools.isVideoFile(task.getmFileName())) {
                    if (task.thumbnail != null) {
                        fileIcon.setImageBitmap(task.thumbnail)
                        filePlayer.visibility = View.VISIBLE
                        itemView.setOnClickListener { torrentInfoView.playerViedo(task) }
                    } else {
                        filePlayer.visibility = View.GONE
                    }
                }
            }


        }

    }
}
