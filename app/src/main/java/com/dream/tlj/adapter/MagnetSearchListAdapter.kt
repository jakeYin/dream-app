package com.dream.tlj.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dream.tlj.R
import com.dream.tlj.mvp.e.MagnetInfo
import com.dream.tlj.mvp.v.MagnetSearchView
import kotlinx.android.synthetic.main.item_search_magnet_result.view.*

class MagnetSearchListAdapter(private val context: Context, private val magnetSearchView: MagnetSearchView, private val list: List<com.dream.tlj.mvp.e.MagnetInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

        fun bind(magnet: com.dream.tlj.mvp.e.MagnetInfo) {
            itemView.magnet_name.text = magnet.name
            itemView.setOnClickListener { magnetSearchView.moreOption(magnet) }
        }

    }
}
