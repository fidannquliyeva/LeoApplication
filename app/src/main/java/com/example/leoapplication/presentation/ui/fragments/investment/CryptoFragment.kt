package com.example.leoapplication.presentation.ui.fragments.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leoapplication.databinding.FragmentCryptoBinding
import com.example.leoapplication.presentation.ui.adapters.CryptoAdapter
import com.example.leoapplication.presentation.viewmodel.CryptoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoFragment : Fragment() {

    private lateinit var binding: FragmentCryptoBinding
    private lateinit var adapter: CryptoAdapter
    private val viewModel: CryptoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CryptoAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter


        observeViewModel()
        viewModel.loadCryptos()

        viewModel.uploadSampleCryptos()
    }


    private fun observeViewModel() {
        viewModel.cryptos.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
    }

}
