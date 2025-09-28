package com.example.leoapplication.presentation.ui.fragments.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentCryptoBinding
import com.example.leoapplication.databinding.FragmentStocksBinding
import com.example.leoapplication.domain.model.Share
import com.example.leoapplication.presentation.ui.adapters.CryptoAdapter
import com.example.leoapplication.presentation.ui.adapters.StocksAdapter
import com.example.leoapplication.presentation.viewmodel.CryptoViewModel
import com.example.leoapplication.presentation.viewmodel.StocksViewModel
import com.google.firebase.firestore.FirebaseFirestore
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

        // RecyclerView setup
        adapter = CryptoAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter



// Məlumatları çəkmək və observer-lə RecyclerView-a ötürmək
        observeViewModel()
        viewModel.loadCryptos()

        // Firestore-a bir dəfəlik məlumat əlavə et
        viewModel.uploadSampleCryptos() // Bir dəfə işlədikdən sonra comment et
    }


    private fun observeViewModel() {
        viewModel.cryptos.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
    }

}
