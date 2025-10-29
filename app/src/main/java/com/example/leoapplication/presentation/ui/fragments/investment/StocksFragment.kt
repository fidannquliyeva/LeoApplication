package com.example.leoapplication.presentation.ui.fragments.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leoapplication.databinding.FragmentStocksBinding
import com.example.leoapplication.presentation.ui.adapters.StocksAdapter
import com.example.leoapplication.presentation.viewmodel.StocksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StocksFragment : Fragment() {

    private lateinit var binding: FragmentStocksBinding
    private lateinit var adapter: StocksAdapter
    private val viewModel: StocksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        adapter = StocksAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter


// Məlumatları çəkmək və observer-lə RecyclerView-a ötürmək
        observeViewModel()
        viewModel.loadShares()

        // Firestore-a bir dəfəlik məlumat əlavə et
//        viewModel.uploadSampleShares() // Bir dəfə işlədikdən sonra comment et
    }


    private fun observeViewModel() {
        viewModel.shares.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
    }

}
