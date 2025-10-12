package com.example.leoapplication.presentation.ui.fragments.increaseBalance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentIncreaseBalanceAmountBinding
import com.example.leoapplication.presentation.viewmodel.IncreaseBalanceViewModel
import com.example.leoapplication.presentation.viewmodel.IncreaseBalanceUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IncreaseBalanceAmountFragment : Fragment() {

    private var _binding: FragmentIncreaseBalanceAmountBinding? = null
    private val binding get() = _binding!!

    // ✅ Eyni ViewModel-i paylaş
    private val viewModel: IncreaseBalanceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncreaseBalanceAmountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAmountInput()
        setupObservers()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar?.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAmountInput() {
        // Məbləğ daxil ediləndə format et
        binding.phoneNumberText.addTextChangedListener { editable ->
            val text = editable.toString()

            // Sadəcə rəqəm və nöqtə qəbul et
            val filtered = text.filter { it.isDigit() || it == '.' }

            // Yalnız bir nöqtə olsun
            val dotCount = filtered.count { it == '.' }
            if (dotCount > 1) {
                val lastDotIndex = filtered.lastIndexOf('.')
                val corrected = filtered.substring(0, lastDotIndex) +
                        filtered.substring(lastDotIndex + 1)
                binding.phoneNumberText.setText(corrected)
                binding.phoneNumberText.setSelection(corrected.length)
                return@addTextChangedListener
            }

            if (filtered != text) {
                binding.phoneNumberText.setText(filtered)
                binding.phoneNumberText.setSelection(filtered.length)
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is IncreaseBalanceUiState.Loading -> {
                            binding.nextButton.isEnabled = false
                            binding.nextButton.text = "Emal edilir..."
                        }
                        is IncreaseBalanceUiState.Success -> {
                            // Uğurlu oldu - success səhifəsinə keç
                            Log.d("IncreaseBalanceAmount", "✅ Success - navigating to success page")
                            navigateToSuccess(state.amount)
                        }
                        is IncreaseBalanceUiState.Error -> {
                            binding.nextButton.isEnabled = true
                            binding.nextButton.text = getString(R.string.balans_art_r)
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            binding.nextButton.isEnabled = true
                            binding.nextButton.text = getString(R.string.balans_art_r)
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.nextButton.setOnClickListener {
            val amountText = binding.phoneNumberText.text.toString()

            if (amountText.isBlank()) {
                Toast.makeText(requireContext(), "Məbləğ daxil edin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()

            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Düzgün məbləğ daxil edin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Xarici kartın nömrəsini ViewModel-dən al
            val externalCardNumber = viewModel.externalCardNumber.value

            if (externalCardNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Kart məlumatları tapılmadı", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
                return@setOnClickListener
            }

            Log.d("IncreaseBalanceAmount", "Processing: Amount=$amount, Card=*${externalCardNumber.replace(" ", "").takeLast(4)}")

            // ✅ Balans artır (indi cardNumber parametri də var)
            viewModel.increaseBalance(amount, externalCardNumber)
        }
    }

    private fun navigateToSuccess(amount: Double) {
        val bundle = Bundle().apply {
            putDouble("amount", amount)
            putString("type", "balance_increase") // ✅ Balans artırma tipi
        }

        findNavController().navigate(
            R.id.action_increaseBalanceOtherCardsFragment_to_paymentSuccessFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}