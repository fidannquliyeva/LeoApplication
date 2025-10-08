package com.example.leoapplication.presentation.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leoapplication.databinding.FragmentHomeBinding
import com.example.leoapplication.presentation.ui.adapters.TransactionAdapter
import com.example.leoapplication.presentation.viewmodel.HomeUiState
import com.example.leoapplication.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearch()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            // Transaction detail-ə keç
            Toast.makeText(
                requireContext(),
                "Məbləğ: ${transaction.amount} ${transaction.currency}\n${transaction.description}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // UI State observer
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is HomeUiState.Loading -> {
                                showLoading(true)
                            }
                            is HomeUiState.Success -> {
                                showLoading(false)
                            }
                            is HomeUiState.Error -> {
                                showLoading(false)
                                val errorMessage = state.message ?: "Xəta baş verdi"
                                Toast.makeText(
                                    requireContext(),
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                // Selected Card observer
                launch {
                    viewModel.selectedCard.collect { card ->
                        card?.let {
                            updateCardUI(it)
                        }
                    }
                }

                // Transactions observer
                launch {
                    viewModel.filteredTransactions.collect { transactions ->
                        transactionAdapter.submitList(transactions)

                        // Empty state
                        if (transactions.isEmpty()) {
                            binding.recyclerView.visibility = View.GONE
                            // TODO: Empty view göstər
                        } else {
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                    }
                }

                // User Data observer (optional)
                launch {
                    viewModel.userData.collect { user ->
                        user?.let {
                            // İstifadəçi adını toolbar-da göstərə bilərsən
                            // binding.homeAppbar.toolbar.title = user.fullName
                        }
                    }
                }
            }
        }
    }

    private fun updateCardUI(card: com.example.leoapplication.data.model.Card) {
        with(binding.homeAppbar) {
            // Böyük balans (expanded)
            balanceNum.text = card.getFormattedBalance()
            balanceNumAzn.text = card.currency

            // Kiçik balans (collapsed)
            balanceNumSmall.text = card.getFormattedBalance()
            balanceNumAznSmall.text = card.currency

            // Kart tipi
            cardNumber.text = card.cardType
        }
    }

    private fun setupClickListeners() {
        with(binding.homeAppbar) {

            // Balans artırılması
            addButton.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Balans artırılması",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Navigate to TopUpFragment
            }

            // Karta köçürmə
            nextButton.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Karta köçürmə",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Navigate to TransferFragment
            }

            // Əməliyyatlar
            walletButton.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Əməliyyatlar",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Navigate to TransactionsFragment
            }

            // Kart məlumatlarına keç
            cardVisa.setOnClickListener {
                viewModel.selectedCard.value?.let { card ->
                    Toast.makeText(
                        requireContext(),
                        "Kart: ${card.cardNumber}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Navigate to CardFragment
                    // val action = HomeFragmentDirections.actionHomeToCard(card.cardId)
                    // findNavController().navigate(action)
                }
            }
        }
    }

    private fun setupSearch() {
        // Search açma
        binding.imgSearch.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE
            binding.imgSearch.visibility = View.GONE
            binding.searchView.requestFocus()
        }

        // Search bağlama
        binding.btnCloseSearch.setOnClickListener {
            binding.searchView.visibility = View.GONE
            binding.imgSearch.visibility = View.VISIBLE
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
            viewModel.searchTransactions("")
        }

        // Search query listener
        binding.searchView.setOnQueryTextListener(
            object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.searchTransactions(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { viewModel.searchTransactions(it) }
                    return true
                }
            }
        )
    }

    private fun showLoading(show: Boolean) {
        // TODO: ProgressBar əlavə et və göstər/gizlət
        // binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}