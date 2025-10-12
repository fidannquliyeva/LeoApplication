package com.example.leoapplication.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leoapplication.data.model.ContactWithLeo
import com.example.leoapplication.databinding.ItemRecentContactBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RecentContactsAdapter(
    private val onItemClick: (ContactWithLeo) -> Unit
) : ListAdapter<ContactWithLeo, RecentContactsAdapter.ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemRecentContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContactViewHolder(
        private val binding: ItemRecentContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: ContactWithLeo) {
            with(binding) {
                // İsim
                tvName.text = contact.name

                // Telefon
                tvPhone.text = contact.phoneNumber

                // Son köçürmə tarixi (varsa)
                if (contact.lastTransferDate != null) {
                    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("az"))
                    tvLastTransfer.text = "Son: ${dateFormat.format(Date(contact.lastTransferDate))}"
                    tvLastTransfer.visibility = android.view.View.VISIBLE
                } else {
                    tvLastTransfer.visibility = android.view.View.GONE
                }

                // Click
                root.setOnClickListener { onItemClick(contact) }
            }
        }
    }

    private class ContactDiffCallback : DiffUtil.ItemCallback<ContactWithLeo>() {
        override fun areItemsTheSame(oldItem: ContactWithLeo, newItem: ContactWithLeo): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: ContactWithLeo, newItem: ContactWithLeo): Boolean {
            return oldItem == newItem
        }
    }
}