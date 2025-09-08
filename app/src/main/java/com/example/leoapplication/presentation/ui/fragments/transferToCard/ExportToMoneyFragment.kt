package com.example.leoapplication.presentation.ui.fragments.transferToCard

import PaymentRepository
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.data.repository.BankCardRepositoryImpl
import com.example.leoapplication.databinding.FragmentExportToMoneyBinding
import com.example.leoapplication.domain.model.Payment
import com.example.leoapplication.domain.model.PaymentType
import com.example.leoapplication.domain.repository.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class ExportToMoneyFragment : Fragment() {

    private lateinit var binding: FragmentExportToMoneyBinding
    private var receiverCardId: String? = null
    private var senderCardId: String? = null // 🔹 artıq dinamik olacaq

    private val firestore = FirebaseFirestore.getInstance()
    private val bankCardRepository = BankCardRepositoryImpl(firestore)
    private val transactionRepository = TransactionRepository(firestore)
    private val paymentRepository = PaymentRepository(firestore, bankCardRepository, transactionRepository)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExportToMoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar2.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        receiverCardId = arguments?.getString("receiverCardId")

        // 🔹 Hazırkı istifadəçinin kartını götür
        lifecycleScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                val snapshot = firestore.collection("bankCards")
                    .whereEqualTo("ownerUserId", currentUserId)
                    .get()
                    .await()

                val firstCard = snapshot.documents.firstOrNull()
                senderCardId = firstCard?.getString("cardNumber")
            }
        }

        binding.btnPay.setOnClickListener {
            val rawInput = binding.edtMoney.text.toString()               // Məsələn: "10 ₼"
            val cleanedInput = rawInput.replace("[^\\d.]".toRegex(), "")  // Rəqəm və nöqtə saxlanır
            val amount = cleanedInput.toDoubleOrNull()

            if (amount != null && !receiverCardId.isNullOrEmpty()) {
                sendMoney(receiverCardId!!, senderCardId.toString(), amount)
            } else {
                Toast.makeText(context, "Miqdarı və kartları yoxlayın", Toast.LENGTH_SHORT).show()
            }
        }
    }
        @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMoney(receiverCardId: String, senderCardId: String, amount: Double) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(context, "İstifadəçi login olmayıb", Toast.LENGTH_SHORT).show()
            return
        }

        val payment = Payment(
            id = (0..100000).random(),
            paymentNumber = "PMT-${System.currentTimeMillis()}",
            amount = amount,
            paymentDate = LocalDateTime.now(),
            senderCardId = senderCardId,       // 🔹 String, artıq Int çevirməyə ehtiyac yoxdur
            receiverCardId = receiverCardId,   // 🔹 String?, Int çevirməyə ehtiyac yoxdur
            paymentType = PaymentType.USERTOUSER,
            paymentTitle = "Karta köçürmə",
            subscriberNumber = null,
            userId = currentUser.uid           // ✅ istifadəçi UID-i
        )

        lifecycleScope.launch {
            val success = paymentRepository.addPayment(payment)
            if (success) {
                val bundle = Bundle().apply {
                    putDouble("amount", amount)
                    putString("receiverCardId", receiverCardId)
                }
                findNavController().navigate(
                    R.id.action_exportToMoneyFragment_to_paymentSuccessFragment,
                    bundle
                )
            } else {
                Toast.makeText(context, "Xəta baş verdi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
