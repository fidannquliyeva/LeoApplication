package com.example.leoapplication.presentation.ui.fragments.investment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentFundsBinding
import com.example.leoapplication.presentation.ui.adapters.FundsAdapter
import com.example.leoapplication.presentation.viewmodel.FundsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FundsFragment : Fragment() {

    private lateinit var binding: FragmentFundsBinding
    private lateinit var adapter: FundsAdapter
    private val viewModel: FundsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFundsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        adapter = FundsAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter



// Məlumatları çəkmək və observer-lə RecyclerView-a ötürmək
        observeViewModel()
       viewModel.loadFunds()

        // Firestore-a bir dəfəlik məlumat əlavə et
        viewModel.uploadSampleFunds() // Bir dəfə işlədikdən sonra comment et
    }


    private fun observeViewModel() {
        viewModel.funds.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
    }

}
