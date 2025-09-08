package com.example.leoapplication.presentation.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.leoapplication.databinding.FragmentCurrencyDialogBinding
import com.example.leoapplication.presentation.viewmodel.CurrencyVM
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCurrencyDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CurrencyVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyDialogBinding.inflate(inflater, container, false)

        viewModel.rates.observe(viewLifecycleOwner) { response ->
            binding.tvUsd.text = "USD: ${response.conversion_rates["USD"] ?: "-"}"
            binding.tvEur.text = "EUR: ${response.conversion_rates["EUR"] ?: "-"}"
            binding.tvGbp.text = "GBP: ${response.conversion_rates["GBP"] ?: "-"}"
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.fetchRates()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
