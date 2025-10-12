package com.example.leoapplication.presentation.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentTransactionDetailBinding
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firestoreDataSource: FirestoreDataSource

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        loadTransactionDetails()
    }

    private fun setupToolbar() {
        binding.toolbar2.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun loadTransactionDetails() {

        val transactionId = arguments?.getString("transactionId") ?: ""
        val amount = arguments?.getDouble("amount") ?: 0.0
        val timestamp = arguments?.getLong("timestamp") ?: 0L
        val description = arguments?.getString("description") ?: ""
        val fromUserId = arguments?.getString("fromUserId") ?: ""
        val toUserId = arguments?.getString("toUserId") ?: ""

        Log.d("TransactionDetail", "Transaction ID: $transactionId")
        Log.d("TransactionDetail", "Amount: $amount")

        val currentUserId = auth.currentUser?.uid


        val isOutgoing = fromUserId == currentUserId

        val formattedAmount = if (isOutgoing) {
            "-${String.format("%.2f", amount)} ₼"
        } else {
            "+${String.format("%.2f", amount)} ₼"
        }

        binding.tvAmount.text = formattedAmount
        binding.tvAmount.setTextColor(
            if (isOutgoing) {
                resources.getColor(android.R.color.holo_red_dark, null)
            } else {
                resources.getColor(android.R.color.holo_green_dark, null)
            }
        )

        // Tarix
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("az"))
        val date = dateFormat.format(Date(timestamp))
        binding.tvSubtitle.text = date

        // Qarşı tərəfin adını yüklə
        val otherUserId = if (isOutgoing) toUserId else fromUserId
        loadUserName(otherUserId, isOutgoing)

        // Description
        if (description.isNotEmpty()) {
            // Əgər layout-da description üçün TextView varsa
            // binding.tvDescription?.text = description
        }

        // Icon
        binding.imgIcon.setImageResource(
            if (isOutgoing) {
                R.drawable.ic_arraw_up_circle
            } else {
                R.drawable.ic_arraw_down_circle
            }
        )
    }

    private fun loadUserName(userId: String, isOutgoing: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = firestoreDataSource.getUser(userId)

            if (result.isSuccess) {
                val user = result.getOrNull()
                val name = user?.fullName ?: "İstifadəçi"

                val title = if (isOutgoing) {
                    "Göndərildi: $name"
                } else {
                    "Alındı: $name"
                }

                binding.tvTitle.text = title
                Log.d("TransactionDetail", "User name loaded: $name")
            } else {
                binding.tvTitle.text = if (isOutgoing) {
                    "Göndərildi"
                } else {
                    "Alındı"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}