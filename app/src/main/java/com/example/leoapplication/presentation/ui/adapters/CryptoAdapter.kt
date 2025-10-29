package com.example.leoapplication.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.leoapplication.R
import com.example.leoapplication.databinding.ItemCryptoBinding
import com.example.leoapplication.data.model.Crypto


class CryptoAdapter(private var list: List<Crypto>) :
    RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    class CryptoViewHolder(val binding: ItemCryptoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val binding = ItemCryptoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CryptoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvPrice.text = "${item.priceUsd} $"
        holder.binding.tvSymbol.text = item.symbol

        Glide.with(holder.itemView.context)
            .load(item.logoUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.icpasscode)
            .into(holder.binding.ivLogo)
    }

        override fun getItemCount() = list.size

    fun updateData(newList: List<Crypto>) {
        list = newList
        notifyDataSetChanged()
    }
}