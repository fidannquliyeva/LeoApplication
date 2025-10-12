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
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        transactionAdapter = TransactionAdapter(currentUserId) { transaction ->
            navigateToTransactionDetail(transaction)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            setHasFixedSize(true)
        }

        setupSwipeToDelete()
    }

    private fun setupSwipeToDelete() {
        val swipeCallback = com.example.leoapplication.presentation.ui.utils.SwipeDeleteCallback { position ->
            val transaction = transactionAdapter.currentList[position]
            deleteTransactionWithUndo(transaction, position)
        }

        val itemTouchHelper = androidx.recyclerview.widget.ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun deleteTransactionWithUndo(
        transaction: com.example.leoapplication.data.model.Transaction,
        position: Int
    ) {
        viewModel.deleteTransaction(transaction)

        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            "Transaction silindi",
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).setAction("GERİ AL") {
            viewModel.restoreTransaction(transaction)
        }.show()
    }

    private fun navigateToTransactionDetail(transaction: com.example.leoapplication.data.model.Transaction) {
        try {
            val bundle = Bundle().apply {
                putString("transactionId", transaction.transactionId)
                putDouble("amount", transaction.amount)
                putLong("timestamp", transaction.timestamp)
                putString("description", transaction.description)
                putString("fromUserId", transaction.fromUserId)
                putString("toUserId", transaction.toUserId)
            }

            Log.d("HomeFragment", "Navigating to transaction detail: ${transaction.transactionId}")

            findNavController().navigate(
                R.id.action_nav_home_to_transactionDetailFragment,
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
                findNavController().navigate(
                    R.id.action_nav_home_to_increaseBalanceFragment
                )
            }

            nextButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_nav_home_to_exportToFragment
                )
            }

            walletButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_nav_home_to_otherPaysFragment
                )
            }

            cardVisa.setOnClickListener {
                viewModel.selectedCard.value?.let { card ->
                    navigateToCardDetails(card.cardId)
                }
            }
        }
    }

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
        binding.progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    // ✅ onResume artıq lazım deyil - real-time sync var!
    // Amma user data refresh etmək istəyirsinizsə, saxlaya bilərsiniz:
    override fun onResume() {
        super.onResume()
        Log.d("HomeFragment", "onResume")
        // ❌ viewModel.refresh() - artıq transactions yeniləməyə ehtiyac yoxdur!
        // Transactions avtomatik real-time update olunur
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}