package com.example.leoapplication.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.leoapplication.databinding.ItemStocksBinding
import com.example.leoapplication.data.model.Share


class StocksAdapter(private var list: List<Share>) :
    RecyclerView.Adapter<StocksAdapter.StocksViewHolder>() {

    class StocksViewHolder(val binding: ItemStocksBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {
        val binding = ItemStocksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StocksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvPrice.text = "${item.price} $"
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.binding.ivLogo)
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Share>) {
        list = newList
        notifyDataSetChanged()
    }
}
