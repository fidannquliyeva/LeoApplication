package com.example.leoapplication.presentation.ui.adapters

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.leoapplication.R
//import com.example.leoapplication.domain.model.Transaction
//class TransactionAdapter(private val transactions: MutableList<Transaction>) :
//    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val icon: ImageView = itemView.findViewById(R.id.imgIcon)
//        val title: TextView = itemView.findViewById(R.id.tvTitle)
//        val subtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
//        val amount: TextView = itemView.findViewById(R.id.tvAmount)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_transaction, parent, false)
//        return TransactionViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val transaction = transactions[position]
//        holder.title.text = transaction.title
//        holder.subtitle.text = transaction.subtitle
//        holder.amount.text = "${transaction.amount} ₼"
//    }
//
//    override fun getItemCount(): Int = transactions.size
//
//    fun addTransaction(transaction: Transaction) {
//        transactions.add(0, transaction)
//        notifyItemInserted(0)
//    }
//}
