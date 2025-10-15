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
import com.example.leoapplication.databinding.FragmentTransferManualInputBinding

import com.example.leoapplication.presentation.viewmodel.TransferUiState
import com.example.leoapplication.presentation.viewmodel.TransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransferManualInputFragment : Fragment() {

    private var _binding: FragmentTransferManualInputBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransferViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferManualInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshBalance()

        setupToolbar()
        setupCardNumberInput()
        setupObservers()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar2.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupCardNumberInput() {

        binding.cardNumberInput.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                val text = s.toString().replace(" ", "")

                val formatted = StringBuilder()
                for (i in text.indices) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ")
                    }
                    formatted.append(text[i])
                }

                binding.cardNumberInput.setText(formatted.toString())
                binding.cardNumberInput.setSelection(formatted.length)

                isFormatting = false
            }
        })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is TransferUiState.Loading -> {
                            showLoading(true)
                            Log.d("TransferManual", "Loading...")
                        }
                        is TransferUiState.RecipientFound -> {
                            showLoading(false)
                            Log.d("TransferManual", "Recipient found!")


                            findNavController().navigate(
                                R.id.action_exportToNewCardFragment_to_exportToMoneyFragment
                            )

                            viewModel.resetState()
                        }
                        is TransferUiState.Error -> {
                            showLoading(false)
                            Log.e("TransferManual", " Error: ${state.message}")

                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_LONG
                            ).show()

                            // Reset state
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

    private fun setupClickListeners() {
        binding.nextButton.setOnClickListener {
            val cardNumber = binding.cardNumberInput.text.toString().replace(" ", "")

            Log.d("TransferManual", "Card number entered: $cardNumber")

            when {
                cardNumber.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Kart nömrəsi daxil edin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                cardNumber.length != 16 -> {
                    Toast.makeText(
                        requireContext(),
                        "Kart nömrəsi 16 rəqəm olmalıdır",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    Log.d("TransferManual", "Searching for card: $cardNumber")
                    viewModel.findUserByCardNumber(cardNumber)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.nextButton.isEnabled = !isLoading
        binding.nextButton.text = if (isLoading) "Yoxlanılır..." else "Davam et"
        binding.cardNumberInput.isEnabled = !isLoading

        Log.d("TransferManual", "Loading state: $isLoading")
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