//package com.example.leoapplication.presentation.ui.adapters
//
//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.leoapplication.databinding.ItemTransactionBinding
//import com.example.leoapplication.domain.model.Transaction
//
//class TransactionAdapter(
//    private val items: List<Transaction>,
//    private val onItemClick: (Transaction) -> Unit
//) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: Transaction) {
//            binding.tvTitle.text = item.title
//            binding.tvSubtitle.text = item.subtitle
//            binding.tvAmount.text = String.format("%.2f", item.amount)
//
//            binding.tvAmount.setTextColor(
//                if (item.isIncome) Color.parseColor("#4CAF50") else Color.BLACK
//            )
//
//            binding.imgIcon.setImageResource(item.iconRes)
//
//            // Click Listener
//            binding.root.setOnClickListener {
//                onItemClick(item)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        val binding = ItemTransactionBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return TransactionViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        holder.bind(items[position])
//    }
//
//    override fun getItemCount(): Int = items.size
//}
