package com.satnamsinghmaggo.paathapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.satnamsinghmaggo.paathapp.R
import com.satnamsinghmaggo.paathapp.databinding.ItemBaniOrderBinding
import com.satnamsinghmaggo.paathapp.model.Bani

class BaniOrderAdapter(
    private var banis: List<Bani>,
    private val onOrderChanged: (List<Bani>) -> Unit
) : RecyclerView.Adapter<BaniOrderAdapter.BaniViewHolder>() {

    inner class BaniViewHolder(private val binding: ItemBaniOrderBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(bani: Bani) {
            binding.baniName.text = bani.name
            binding.baniTime.text = bani.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaniViewHolder {
        val binding = ItemBaniOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BaniViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaniViewHolder, position: Int) {
        holder.bind(banis[position])
    }

    override fun getItemCount() = banis.size

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val updatedList = banis.toMutableList()
        val movedItem = updatedList.removeAt(fromPosition)
        updatedList.add(toPosition, movedItem)
        banis = updatedList
        notifyItemMoved(fromPosition, toPosition)
        onOrderChanged(banis)
    }

    fun updateList(newList: List<Bani>) {
        banis = newList
        notifyDataSetChanged()
    }
} 