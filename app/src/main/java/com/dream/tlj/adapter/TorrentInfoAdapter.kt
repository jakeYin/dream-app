package com.dream.tlj.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dream.tlj.R
import com.dream.tlj.mvp.e.TorrentInfoEntity
import com.dream.tlj.mvp.v.TorrentInfoView
import com.dream.tlj.util.FileTools
import kotlinx.android.synthetic.main.item_torrent_info.view.*

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
        fun bind(task: TorrentInfoEntity) {
            this.task = task
            val suffix = task.getmFileName()!!.substring(task.getmFileName()!!.lastIndexOf(".") + 1)
            itemView.file_name.text = task.getmFileName()

            itemView.file_size.text = FileTools.convertFileSize(task.getmFileSize())
            itemView.file_type.setText(suffix)
            if (torrentInfoView.isDown) {
                itemView.file_icon.setImageDrawable(itemView.resources.getDrawable(FileTools.getFileIcon(task.getmFileName())))
                if (task.check!!) {
                    itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_check))
                } else {
                    itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_uncheck))
                }
            } else {
                itemView.file_check_box.visibility = View.GONE
                itemView.file_icon.setImageDrawable(itemView.resources.getDrawable(FileTools.getFileIcon(task.getmFileName())))
                if (FileTools.isVideoFile(task.getmFileName())) {
                    if (task.thumbnail != null) {
                        itemView.file_icon.setImageBitmap(task.thumbnail)
                        itemView.file_play.visibility = View.VISIBLE
                        itemView.setOnClickListener { torrentInfoView.playVideo(task) }
                    } else {
                        itemView.file_play.visibility = View.GONE
                    }
                }
            }


        }

    }
}
