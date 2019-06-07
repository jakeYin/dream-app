package cn.sddman.download.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.sddman.download.R
import cn.sddman.download.mvp.v.SourceDetailView

class SourceDetailListAdapter(private val context: Context, private val sourceView: SourceDetailView, private val list: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        private val magnetNameText: TextView
        private val magnetDate: TextView

        init {
            magnetNameText = itemView.findViewById<View>(R.id.magnet_name) as TextView
            magnetDate = itemView.findViewById<View>(R.id.magnet_date) as TextView
        }

        fun bind(name:String) {
            magnetNameText.text = name
            itemView.setOnClickListener { sourceView.clickItem(name) }
        }
    }
}
