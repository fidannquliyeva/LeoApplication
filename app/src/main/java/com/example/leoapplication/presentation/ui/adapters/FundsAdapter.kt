package com.example.leoapplication.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.leoapplication.R
import com.example.leoapplication.databinding.ItemFundsBinding
import com.example.leoapplication.data.model.Funds

class FundsAdapter (private var list: List<Funds>) :
    RecyclerView.Adapter<FundsAdapter.FundsViewHolder>() {

    class FundsViewHolder(val binding: ItemFundsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundsViewHolder {
        val binding = ItemFundsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FundsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FundsViewHolder, position: Int) {
        val item = list[position]
        holder.binding.fundName.text = item.name
        holder.binding.fundValue.text = "${item.value} $"
        holder.binding.fundSymbol.text = item.symbol

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.icpasscode)
            .into(holder.binding.fundImage)
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Funds>) {
        list = newList
        notifyDataSetChanged()
    }
}