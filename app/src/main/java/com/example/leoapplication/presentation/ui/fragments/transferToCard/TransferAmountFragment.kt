package com.example.leoapplication.presentation.ui.fragments.transferToCard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentTransferAmountBinding
import com.example.leoapplication.presentation.viewmodel.TransferUiState
import com.example.leoapplication.presentation.viewmodel.TransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransferAmountFragment : Fragment() {

    private var _binding: FragmentTransferAmountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransferViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferAmountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("TransferAmount", "====== OPENED ======")

        viewModel.refreshBalance()

        setupToolbar()
        setupObservers()
        setupAmountInput()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar2.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.currentBalance.collect { balance ->
                        binding.txtBalance.text = "Balans: ${String.format("%.2f", balance)} ₼"
                        Log.d("TransferAmount", "Balance: $balance")
                    }
                }


                launch {
                    viewModel.recipientName.collect { name ->
                        if (name.isNotEmpty()) {
                            binding.tvTitle.text = name
                            Log.d("TransferAmount", "Recipient: $name")
                        } else {
                            binding.tvTitle.text = "Kart sahibi"
                        }
                    }
                }


                launch {
                    viewModel.recipientPhoneNumber.collect { phone ->
                        binding.tvSubtitle.text = phone
                        Log.d("TransferAmount", "Phone: $phone")
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is TransferUiState.Loading -> {
                                showLoading(true)
                                Log.d("TransferAmount", "Loading...")
                            }
                            is TransferUiState.TransferSuccess -> {
                                showLoading(false)
                                Log.d("TransferAmount", "✅ Transfer success!")

                                Toast.makeText(
                                    requireContext(),
                                    "Transfer uğurla tamamlandı!",
                                    Toast.LENGTH_SHORT
                                ).show()
//success
                                val bundle = Bundle().apply {
                                    putString("transactionId", state.transaction.transactionId)
                                    putDouble("amount", state.transaction.amount)
                                    putString("recipientName", binding.tvTitle.text.toString())
                                }

                                findNavController().navigate(
                                    R.id.action_transferAmountFragment_to_paymentSuccessFragment,
                                    bundle
                                )

                                viewModel.clearTransferData()
                            }
                            is TransferUiState.Error -> {
                                showLoading(false)
                                Log.e("TransferAmount", " Error: ${state.message}")

                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_LONG
                                ).show()


                                viewModel.resetState()
                            }
                            else -> {
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupAmountInput() {
        binding.edtMoney.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty() && text != "0") {
                    try {
                        val amount = text.toDouble()
                        viewModel.setAmount(amount)
                        Log.d("TransferAmount", "Amount set: $amount")
                    } catch (e: Exception) {
                        Log.e("TransferAmount", "Invalid amount: $text")
                    }
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnPay.setOnClickListener {
            val amountText = binding.edtMoney.text.toString()

            Log.d("TransferAmount", "Pay button clicked")

            if (amountText.isEmpty() || amountText == "0") {
                Toast.makeText(
                    requireContext(),
                    "Məbləğ daxil edin",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            try {
                val amount = amountText.toDouble()


                if (amount <= 0) {
                    Toast.makeText(
                        requireContext(),
                        "Məbləğ 0-dan böyük olmalıdır",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (amount > viewModel.currentBalance.value) {
                    Toast.makeText(
                        requireContext(),
                        "Balans kifayət deyil",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                Log.d("TransferAmount", "Performing transfer: $amount")
                viewModel.setAmount(amount)
                viewModel.performTransfer()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Düzgün məbləğ daxil edin",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnPay.isEnabled = !isLoading
        binding.btnPay.text = if (isLoading) "Gözləyin..." else "Göndər"
        binding.edtMoney.isEnabled = !isLoading

        Log.d("TransferAmount", "Loading state: $isLoading")
    }


    override fun onResume() {
        super.onResume()
        viewModel.refreshBalance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}