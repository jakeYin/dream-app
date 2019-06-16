package cn.sddman.download.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.sddman.download.R
import cn.sddman.download.mvp.e.MagnetDetail
import cn.sddman.download.mvp.v.SourceDetailView
import kotlinx.android.synthetic.main.item_downloading.view.*
import kotlinx.android.synthetic.main.item_search_magnet_result.view.*

class SourceDetailListAdapter(private val context: Context, private val sourceView: SourceDetailView, private val list: List<MagnetDetail>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_source_rv, viewGroup, false)
        return MagnetHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val task = list[i]
        val holder = viewHolder as MagnetHolder
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal inner class MagnetHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(detail:MagnetDetail) {
            itemView.magnet_name.text = detail.name
            if (detail.check) {
                itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_check))
            } else {
                itemView.file_check_box.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_uncheck))
            }
            itemView.file_check_box.setOnClickListener {
                detail.check = !detail.check
                notifyDataSetChanged()
            }
        }
    }
}
