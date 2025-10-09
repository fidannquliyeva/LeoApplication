package com.example.leoapplication.presentation.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentHomeBinding
import com.example.leoapplication.presentation.ui.adapters.TransactionAdapter
import com.example.leoapplication.presentation.viewmodel.HomeUiState
import com.example.leoapplication.presentation.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        checkAuth()
        checkFirestoreCards()

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearch()
    }

    private fun checkAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("HomeFragment", "====== AUTH TEST ======")
        Log.d("HomeFragment", "User ID: ${currentUser?.uid}")
        Log.d("HomeFragment", "Is logged in: ${currentUser != null}")
    }

    private fun checkFirestoreCards() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("cards")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("HomeFragment", "Cards found: ${documents.size()}")
            }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { transaction ->
            Toast.makeText(
                requireContext(),
                "Məbləğ: ${transaction.amount} ${transaction.currency}",
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

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is HomeUiState.Loading -> showLoading(true)
                            is HomeUiState.Success -> showLoading(false)
                            is HomeUiState.Error -> {
                                showLoading(false)
                                Toast.makeText(
                                    requireContext(),
                                    state.message ?: "Xəta",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.selectedCard.collect { card ->
                        card?.let { updateCardUI(it) }
                    }
                }

                launch {
                    viewModel.filteredTransactions.collect { transactions ->
                        transactionAdapter.submitList(transactions)
                        binding.recyclerView.visibility =
                            if (transactions.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }

    private fun updateCardUI(card: com.example.leoapplication.data.model.Card) {
        with(binding.homeAppbar) {
            balanceNum.text = card.getFormattedBalance()
            balanceNumAzn.text = card.currency
            balanceNumSmall.text = card.getFormattedBalance()
            balanceNumAznSmall.text = card.currency
            cardNumber.text = card.cardType
        }
    }

    private fun setupClickListeners() {
        with(binding.homeAppbar) {

            addButton.setOnClickListener {
                // Navigate to IncreaseBalance
                findNavController().navigate(
                    R.id.action_nav_home_to_increaseBalanceFragment
                )
            }

            nextButton.setOnClickListener {
                // Navigate to Export/Transfer
                findNavController().navigate(
                    R.id.action_nav_home_to_exportToFragment
                )
            }

            walletButton.setOnClickListener {
                // Navigate to Other Pays
                findNavController().navigate(
                    R.id.action_nav_home_to_otherPaysFragment
                )
            }

            // ✅ KART CLICK - Navigation Component ilə
            cardVisa.setOnClickListener {
                viewModel.selectedCard.value?.let { card ->
                    navigateToCardDetails(card.cardId)
                }
            }
        }
    }

    // ✅ Navigation with Safe Args
    private fun navigateToCardDetails(cardId: String) {
        Log.d("HomeFragment", "Navigating to card: $cardId")

        try {
            val bundle = Bundle().apply {
                putString("cardId", cardId)
            }

            findNavController().navigate(
                R.id.action_nav_home_to_cardFragment,
                bundle
            )
        } catch (e: Exception) {
            Log.e("HomeFragment", "Navigation error: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Xəta: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupSearch() {
        binding.imgSearch.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE
            binding.imgSearch.visibility = View.GONE
            binding.searchView.requestFocus()
        }

        binding.btnCloseSearch.setOnClickListener {
            binding.searchView.visibility = View.GONE
            binding.imgSearch.visibility = View.VISIBLE
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
            viewModel.searchTransactions("")
        }

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
        // TODO: ProgressBar
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}