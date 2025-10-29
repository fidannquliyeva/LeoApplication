package com.example.leoapplication.presentation.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentPaymentSuccessBinding
import com.example.leoapplication.presentation.viewmodel.IncreaseBalanceViewModel
import com.example.leoapplication.presentation.viewmodel.TransferViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class PaymentSuccessFragment : Fragment() {

    private var _binding: FragmentPaymentSuccessBinding? = null
    private val binding get() = _binding!!

    private val transferViewModel: TransferViewModel by activityViewModels()
    private val increaseBalanceViewModel: IncreaseBalanceViewModel by activityViewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val type = arguments?.getString("type") ?: "transfer"

        when (type) {
            "balance_increase" -> setupBalanceIncreaseUI()
            else -> setupTransferUI()
        }

        setupClickListeners(type)
    }

    private fun setupBalanceIncreaseUI() {
        val amount = arguments?.getDouble("amount") ?: 0.0

        binding.tvAmount.text = String.format("%.2f ₼", amount)

        binding.tvStatus.text = "✔ Uğurla balans artırıldı"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    increaseBalanceViewModel.selectedCard.collect { card ->
                        card?.let {
                            val cardLast4 = it.cardNumber.replace(" ", "").takeLast(4)
                            binding.tvReceiver.text = "*$cardLast4 | Leobank\n${it.cardHolderName}"
                            Log.d("PaymentSuccess", "Receiver card: *$cardLast4")
                        }
                    }
                }


                launch {
                    increaseBalanceViewModel.externalCardNumber.collect { externalCard ->
                        if (externalCard.isNotEmpty()) {
                            val last4 = externalCard.replace(" ", "").takeLast(4)
                            binding.tvSender.text = "Xarici Bank\n*$last4"
                            Log.d("PaymentSuccess", "Sender card: *$last4")
                        } else {
                            binding.tvSender.text = "Xarici Bank\nBalans Artırma"
                        }
                    }
                }
            }
        }

        val dateFormat = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale("az"))
        binding.tvDate.text = dateFormat.format(Date())
    }

    private fun setupTransferUI() {
        val transactionId = arguments?.getString("transactionId") ?: ""
        val amount = arguments?.getDouble("amount") ?: 0.0
        val recipientName = arguments?.getString("recipientName") ?: "Alıcı"


        binding.tvAmount.text = String.format("%.2f ₼", amount)
        binding.tvStatus.text = "✔ Uğurla köçürüldü"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Alıcı
                launch {
                    transferViewModel.recipientName.collect { name ->
                        if (name.isNotEmpty()) {
                            binding.tvReceiver.text = "$name | Leobank"
                        } else {
                            binding.tvReceiver.text = "$recipientName | Leobank"
                        }
                    }
                }

                launch {
                    transferViewModel.currentBalance.collect { _ ->
                        val currentUser = auth.currentUser
                        val senderName = currentUser?.displayName ?: "Siz"
                        binding.tvSender.text = "$senderName\nLeobank"
                    }
                }
            }
        }

        val dateFormat = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale("az"))
        binding.tvDate.text = dateFormat.format(Date())
    }

    private fun setupClickListeners(type: String) {
        binding.btnReady.setOnClickListener {
            Log.d("PaymentSuccess", "Ready button clicked - going to home")

            when (type) {
                "balance_increase" -> {
                    increaseBalanceViewModel.resetState()

                }
                else -> {
                    transferViewModel.clearTransferData()
                    transferViewModel.resetState()
                    Log.d("PaymentSuccess", "Transfer state reset")
                }
            }

            findNavController().popBackStack(R.id.nav_home, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}