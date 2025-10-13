package com.example.leoapplication.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leoapplication.R
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionStatus
import com.example.leoapplication.data.model.TransactionType
import com.example.leoapplication.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val currentUserId: String,
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            with(binding) {
                // Tarix formatı
                val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("az"))
                tvDate.text = dateFormat.format(Date(transaction.timestamp))

                val isOutgoing = when (transaction.type) {
                    TransactionType.BALANCE_INCREASE -> false // ✅ Həmişə incoming
                    else -> transaction.fromUserId == currentUserId
                }

                tvDescription.text = when (transaction.type) {
                    TransactionType.TRANSFER -> {
                        if (isOutgoing) "Göndərildi" else "Alındı"
                    }
                    TransactionType.BALANCE_INCREASE -> "Balans artırma" // ✅ YENİ
                    TransactionType.PAYMENT -> "Ödəniş"
                    TransactionType.DEPOSIT -> "Depozit"
                    TransactionType.WITHDRAWAL -> "Çıxarış"
                    else -> {
                        if (transaction.description.isNotEmpty()) {
                            transaction.description
                        } else {
                            "Əməliyyat"
                        }
                    }
                }

                // Amount formatı
                val amountText = if (isOutgoing) {
                    "-${String.format("%.2f", transaction.amount)} ${transaction.currency}"
                } else {
                    "+${String.format("%.2f", transaction.amount)} ${transaction.currency}"
                }
                tvAmount.text = amountText

                // Rəng
                val color = if (isOutgoing) {
                    ContextCompat.getColor(root.context, android.R.color.holo_red_dark)
                } else {
                    ContextCompat.getColor(root.context, android.R.color.holo_green_dark)
                }
                tvAmount.setTextColor(color)

                // Status icon
                val statusIcon = when (transaction.status) {
                    TransactionStatus.COMPLETED -> R.drawable.ic_check_circle
                    TransactionStatus.PENDING -> R.drawable.ic_pending
                    TransactionStatus.FAILED -> R.drawable.ic_error
                    TransactionStatus.CANCELLED -> R.drawable.ic_cancel
                }
                ivStatus.setImageResource(statusIcon)

                // Click
                root.setOnClickListener { onItemClick(transaction) }
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}