package com.satnamsinghmaggo.paathapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.satnamsinghmaggo.paathapp.R
import com.satnamsinghmaggo.paathapp.model.Bani

class BaniAdapter(
    private var banis: List<Bani>,
    private val onItemClick: (Bani) -> Unit
) : RecyclerView.Adapter<BaniAdapter.BaniViewHolder>() {

    class BaniViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvBaniName)
        val tvTime: TextView = itemView.findViewById(R.id.tvBaniTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaniViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bani, parent, false)
        return BaniViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaniViewHolder, position: Int) {
        val bani = banis[position]
        holder.tvName.text = bani.name
        holder.tvTime.text = bani.time
        holder.itemView.setOnClickListener { onItemClick(bani) }
    }

    override fun getItemCount() = banis.size

    fun updateBanis(newBanis: List<Bani>) {
        banis = newBanis
        notifyDataSetChanged()
    }
} 