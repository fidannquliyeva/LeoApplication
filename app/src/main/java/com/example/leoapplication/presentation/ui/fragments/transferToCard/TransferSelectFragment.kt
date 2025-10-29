package com.example.leoapplication.presentation.ui.fragments.transferToCard

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentTransferSelectBinding
import com.example.leoapplication.presentation.ui.adapters.RecentContactsAdapter
import com.example.leoapplication.presentation.viewmodel.TransferUiState
import com.example.leoapplication.presentation.viewmodel.TransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransferSelectFragment : Fragment() {

    private var _binding: FragmentTransferSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransferViewModel by activityViewModels()

    private lateinit var contactsAdapter: RecentContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearch()

        viewModel.loadRecentTransfers()
    }

    private fun setupToolbar() {
        binding.toolbar2.apply {
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() {
        contactsAdapter = RecentContactsAdapter { contact ->
            viewModel.selectRecipient(contact)

            findNavController().navigate(
                R.id.action_transferSelectFragment_to_transferAmountFragment
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is TransferUiState.Loading -> {
                                binding.progressBar?.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                            }

                            is TransferUiState.Success -> {
                                binding.progressBar?.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                            }

                            is TransferUiState.Error -> {
                                binding.progressBar?.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.filteredContacts.collect { contacts ->
                        contactsAdapter.submitList(contacts)

                        if (contacts.isEmpty()) {

                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.linearLayoutNewPay.setOnClickListener {

            findNavController().navigate(
                R.id.action_transferSelectFragment_to_transferManualInputFragment
            )
        }
    }


    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(
            object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.searchContacts(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { viewModel.searchContacts(it) }
                    return true
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
