package cn.sddman.download.adapter

import android.content.Context
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
import cn.sddman.download.mvp.e.MagnetInfo
import cn.sddman.download.mvp.v.MagnetSearchView
import cn.sddman.download.util.FileTools
import cn.sddman.download.util.TimeUtil

class MagnetSearchListAdapter(private val context: Context, private val magnetSearchView: MagnetSearchView, private val list: List<MagnetInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_search_magnet_result, viewGroup, false)
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
        private val magnetSize: TextView
        private val magnetDate: TextView
        private val magnetHot: TextView

        init {
            magnetNameText = itemView.findViewById<View>(R.id.magnet_name) as TextView
            magnetSize = itemView.findViewById<View>(R.id.magnet_size) as TextView
            magnetDate = itemView.findViewById<View>(R.id.magnet_date) as TextView
            magnetHot = itemView.findViewById<View>(R.id.magnet_hot) as TextView
        }

        fun bind(magnet: MagnetInfo) {
            magnetNameText.text = magnet.name
//            magnetDate.setText(String.format(itemView.resources.getString(R.string.magnet_date), magnet.count))
//            magnetSize.setText(String.format(itemView.resources.getString(R.string.magnet_size), magnet.formatSize))
//            magnetHot.setText(String.format(itemView.resources.getString(R.string.magnet_hot), magnet.hot))
            itemView.setOnClickListener { magnetSearchView.moreOption(magnet) }
        }

    }
}
