package com.example.leoapplication.presentation.ui.fragments.home

import androidx.fragment.app.Fragment


import dagger.hilt.android.AndroidEntryPoint

// getalltransaction 5 denesi layotlari
@AndroidEntryPoint
class HomeFragment : Fragment() {
//    private lateinit var binding: FragmentHomeBinding
//    private val cardVM: CardVM by activityViewModels()
//    private val loginVM: LoginWithNumberVM by activityViewModels()
//
//    private lateinit var transactionAdapter: TransactionAdapter
//    private val firestore = FirebaseFirestore.getInstance()
//
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupSearchView()
//        navigateToIncreaseBalance()
//        navigateOtherPay()
//
//
//        val recyclerView = binding.recyclerView
//        transactionAdapter = TransactionAdapter(mutableListOf())
//        recyclerView.adapter = transactionAdapter
//        recyclerView.layoutManager = LinearLayoutManager(context)
//
//        listenTransactions()
//
////        // Dummy transaction list
////        val transactions = listOf(
////            Transaction("suku", "Karta köçürmə", 1.00, false, R.drawable.ic_whatsapp),
////            Transaction("Bolt", "Taksi", 2.65, false, R.drawable.icnextpage),
////            Transaction("www.birbank.az", "Kart hesabının artımı", 5.00, true, R.drawable.icnotepad),
////            Transaction("Xuraman Q.", "Kart hesabının artımı", 5.00, true, R.drawable.ic_whatsapp),
////            Transaction("suku", "Karta köçürmə", 1.00, false, R.drawable.ic_whatsapp),
////            Transaction("Bolt", "Taksi", 2.65, false, R.drawable.icnextpage),
////            Transaction("www.birbank.az", "Kart hesabının artımı", 5.00, true, R.drawable.icnotepad),
////            Transaction("Xuraman Q.", "Kart hesabının artımı", 5.00, true, R.drawable.ic_whatsapp),
////            Transaction("suku", "Karta köçürmə", 1.00, false, R.drawable.ic_whatsapp),
////            Transaction("Bolt", "Taksi", 2.65, false, R.drawable.icnextpage),
////            Transaction("www.birbank.az", "Kart hesabının artımı", 5.00, true, R.drawable.icnotepad),
////            Transaction("Xuraman Q.", "Kart hesabının artımı", 5.00, true, R.drawable.ic_whatsapp),
////            Transaction("suku", "Karta köçürmə", 1.00, false, R.drawable.ic_whatsapp),
////            Transaction("Bolt", "Taksi", 2.65, false, R.drawable.icnextpage),
////            Transaction("www.birbank.az", "Kart hesabının artımı", 5.00, true, R.drawable.icnotepad),
////            Transaction("Xuraman Q.", "Kart hesabının artımı", 5.00, true, R.drawable.ic_whatsapp),
////        )
//
////        // Adapter və klik zamanı TransactionDetailFragment-ə keçid
////        val adapter = TransactionAdapter(transactions) { clickedItem ->
////            val bundle = Bundle().apply {
////                putSerializable("transaction", clickedItem)
////            }
////            findNavController().navigate(
////                R.id.action_nav_home_to_transactionDetailFragment,
////                bundle
////            )
////        }
////
////        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
////        binding.recyclerView.adapter = adapter
//
//        // Firebase-dən mövcud kart məlumatını çək
//        val phone = loginVM.phoneNumber
//        if (phone.isNotEmpty()) {
//            cardVM.fetchCardByPhone(phone)
//        }
//
//        // Kart məlumatı gələndə UI göstər
//        cardVM.bankCard.observe(viewLifecycleOwner) { card ->
//            card ?: return@observe
//
//            binding.homeAppbar.balanceNum.text = card.balance.toString()
//            binding.homeAppbar.balanceNumSmall.text = card.balance.toString()
//
//            val appBar = view.findViewById<AppBarLayout>(R.id.app_bar)
//            val cardVisa = view.findViewById<View>(R.id.card_visa)
//            val visaTxt = view.findViewById<TextView>(R.id.card_number)
//
//            // AppBar scroll animasiyası
//            appBar.addOnOffsetChangedListener { _, verticalOffset ->
//                val totalRange = appBar.totalScrollRange
//                val progress = -verticalOffset / totalRange.toFloat()
//
//                if (progress >= 1f) {
//                    cardVisa.visibility = View.GONE
//                    visaTxt.visibility = View.GONE
//                } else {
//                    cardVisa.visibility = View.VISIBLE
//                    visaTxt.visibility = View.VISIBLE
//                    cardVisa.translationY = progress * cardVisa.height
//                    visaTxt.translationY = progress * visaTxt.height
//                }
//            }
//
//            cardVisa.transitionName = "card_transition" // Shared element adı
//
//            cardVisa.setOnClickListener {
//                visaTxt.isInvisible
//                cardVisa.animate()
//                    .rotationY(90f)
//                    .setDuration(200)
//                    .withEndAction {
//                        val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
//                            cardVisa to "card_transition"
//                        )
//                        findNavController().navigate(
//                            R.id.action_nav_home_to_cardFragment,
//                            null,
//                            null,
//                            extras
//                        )
//                    }
//                    .start()
//            }
//        }
//    }
//
//    private fun navigateToIncreaseBalance() {
//        binding.appBar.findViewById<ImageView>(R.id.add_button).setOnClickListener {
//            findNavController().navigate(R.id.action_nav_home_to_increaseBalanceFragment)
//        }
//
//        binding.appBar.findViewById<ImageView>(R.id.next_button).setOnClickListener {
//            findNavController().navigate(R.id.action_nav_home_to_exportToFragment)
//        }
//    }
//      private fun navigateOtherPay(){
//          binding.homeAppbar.walletButton.setOnClickListener {
//              findNavController().navigate(R.id.action_nav_home_to_otherPaysFragment)
//          }
//
//      }
//
//    private fun setupSearchView() {
//        val searchView = binding.searchView
//        val imgSearch = binding.imgSearch
//        val closeButton = binding.btnCloseSearch
//
//        searchView.visibility = View.GONE
//        closeButton.visibility = View.GONE
//
//        imgSearch.setOnClickListener {
//            imgSearch.visibility = View.GONE
//            searchView.visibility = View.VISIBLE
//            closeButton.visibility = View.VISIBLE
//            searchView.isIconified = false
//            searchView.requestFocus()
//        }
//
//        closeButton.setOnClickListener {
//            if (searchView.query.isEmpty()) {
//                searchView.visibility = View.GONE
//                closeButton.visibility = View.GONE
//                imgSearch.visibility = View.VISIBLE
//            } else {
//                searchView.setQuery("", false)
//            }
//        }
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?) = false
//            override fun onQueryTextChange(newText: String?) = false
//        })
//    }
//
//
//    private fun listenTransactions() {
//        firestore.collection("transactions")
//            .orderBy("transactionDate", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshots, error ->
//                if (error != null) return@addSnapshotListener
//                snapshots?.documentChanges?.forEach { docChange ->
//                    if (docChange.type == DocumentChange.Type.ADDED) {
//                        val transaction = docChange.document.toObject(Transaction::class.java)
//                        transactionAdapter.addTransaction(transaction)
//                    }
//                }
//            }
//}
    }
