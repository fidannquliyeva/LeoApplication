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
import com.example.leoapplication.databinding.FragmentIncreaseOtherCardBinding
import com.example.leoapplication.presentation.viewmodel.IncreaseBalanceViewModel
import com.example.leoapplication.presentation.viewmodel.IncreaseBalanceUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IncreaseOtherCardFragment : Fragment() {

    private var _binding: FragmentIncreaseOtherCardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IncreaseBalanceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncreaseOtherCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupCardFormatting()
        setupObservers()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCardFormatting() {

        binding.cardNumberInput.addTextChangedListener { editable ->
            val text = editable.toString().replace(" ", "")

            if (text.length > 16) {
                binding.cardNumberInput.setText(text.substring(0, 16))
                binding.cardNumberInput.setSelection(16)
                return@addTextChangedListener
            }

            val formatted = text.chunked(4).joinToString(" ")
            if (formatted != editable.toString()) {
                binding.cardNumberInput.setText(formatted)
                binding.cardNumberInput.setSelection(formatted.length)
            }
        }

        binding.expiryInput.addTextChangedListener { editable ->
            val text = editable.toString().replace("/", "")

            if (text.length > 4) {
                binding.expiryInput.setText(text.substring(0, 4))
                binding.expiryInput.setSelection(5) // MM/YY = 5 character
                return@addTextChangedListener
            }

            val formatted = if (text.length >= 2) {
                "${text.substring(0, 2)}/${text.substring(2)}"
            } else {
                text
            }

            if (formatted != editable.toString()) {
                binding.expiryInput.setText(formatted)
                binding.expiryInput.setSelection(formatted.length)
            }
        }

        binding.cvvInput.addTextChangedListener { editable ->
            val text = editable.toString()
            if (text.length > 3) {
                binding.cvvInput.setText(text.substring(0, 3))
                binding.cvvInput.setSelection(3)
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
                            binding.nextButton.text = "Yoxlanılır..."
                        }
                        is IncreaseBalanceUiState.CardLoaded -> {
                            binding.nextButton.isEnabled = true
                            binding.nextButton.text = getString(R.string.r_li)
                        }
                        is IncreaseBalanceUiState.Error -> {
                            binding.nextButton.isEnabled = true
                            binding.nextButton.text = getString(R.string.r_li)
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            binding.nextButton.isEnabled = true
                            binding.nextButton.text = getString(R.string.r_li)
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.nextButton.setOnClickListener {
            if (validateInputs()) {

                val cardNumber = binding.cardNumberInput.text.toString()

                viewModel.setExternalCardNumber(cardNumber)

                findNavController().navigate(
                    R.id.action_increaseOtherCardFragment_to_increaseBalanceOtherCardsFragment
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        val cardNumber = binding.cardNumberInput.text.toString().replace(" ", "")
        val expiry = binding.expiryInput.text.toString()
        val cvv = binding.cvvInput.text.toString()

        return when {
            cardNumber.isEmpty() -> {
                Toast.makeText(requireContext(), "Kart nömrəsini daxil edin", Toast.LENGTH_SHORT).show()
                binding.cardNumberInput.requestFocus()
                false
            }
            cardNumber.length != 16 -> {
                Toast.makeText(requireContext(), "Kart nömrəsi 16 rəqəm olmalıdır", Toast.LENGTH_SHORT).show()
                binding.cardNumberInput.requestFocus()
                false
            }
            !isValidCardNumber(cardNumber) -> {
                Toast.makeText(requireContext(), "Kart nömrəsi düzgün deyil", Toast.LENGTH_SHORT).show()
                binding.cardNumberInput.requestFocus()
                false
            }
            expiry.isEmpty() -> {
                Toast.makeText(requireContext(), "Son istifadə tarixini daxil edin", Toast.LENGTH_SHORT).show()
                binding.expiryInput.requestFocus()
                false
            }
            expiry.length != 5 -> {
                Toast.makeText(requireContext(), "Tarix MM/YY formatında olmalıdır", Toast.LENGTH_SHORT).show()
                binding.expiryInput.requestFocus()
                false
            }
            !isValidExpiryDate(expiry) -> {
                Toast.makeText(requireContext(), "Kart tarixi keçmişdir", Toast.LENGTH_SHORT).show()
                binding.expiryInput.requestFocus()
                false
            }
            cvv.isEmpty() -> {
                Toast.makeText(requireContext(), "CVV daxil edin", Toast.LENGTH_SHORT).show()
                binding.cvvInput.requestFocus()
                false
            }
            cvv.length != 3 -> {
                Toast.makeText(requireContext(), "CVV 3 rəqəm olmalıdır", Toast.LENGTH_SHORT).show()
                binding.cvvInput.requestFocus()
                false
            }
            else -> {
                Log.d("IncreaseOtherCard", "Card: $cardNumber, Expiry: $expiry, CVV: $cvv")
                true
            }
        }
    }


    private fun isValidCardNumber(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false

        for (i in cardNumber.length - 1 downTo 0) {
            var digit = cardNumber[i].toString().toInt()

            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit = (digit % 10) + 1
                }
            }

            sum += digit
            alternate = !alternate
        }

        return sum % 10 == 0
    }

    private fun isValidExpiryDate(expiry: String): Boolean {
        try {
            val parts = expiry.split("/")
            if (parts.size != 2) return false

            val month = parts[0].toInt()
            val year = parts[1].toInt()

            if (month < 1 || month > 12) return false

            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
            val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1

            return if (year > currentYear) {
                true
            } else if (year == currentYear) {
                month >= currentMonth
            } else {
                false
            }
        } catch (e: Exception) {
            return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}