package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentHomeBinding
import com.example.leoapplication.presentation.ui.adapters.RecyclerAdapter
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateToIncreaseBalance()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data
        val list = mutableListOf<String>()
        for (i in 1..30) list.add("Item #$i")
        recyclerView.adapter = RecyclerAdapter(list)

        val appBar = view.findViewById<AppBarLayout>(R.id.app_bar)
        val balanceNum = view.findViewById<TextView>(R.id.balance_num_app)
        val cardVisa = view.findViewById<View>(R.id.card_visa)
        val visaTxt = view.findViewById<TextView>(R.id.card_number)



        // AppBar scroll animasiyası
        appBar.addOnOffsetChangedListener { _, verticalOffset ->
            val totalRange = appBar.totalScrollRange
            val progress = -verticalOffset / totalRange.toFloat()



            if (progress >= 1f) {
                // tam gizlə
                cardVisa.visibility = View.GONE
                visaTxt.visibility = View.GONE
            } else {
                // hərəkət edib aşağıya girsin
                cardVisa.visibility = View.VISIBLE
                visaTxt.visibility = View.VISIBLE

                cardVisa.translationY = progress * cardVisa.height
                visaTxt.translationY = progress * visaTxt.height
            }
        }


        cardVisa.transitionName = "card_transition" // Shared element adı

        cardVisa.setOnClickListener {
            // Flip animasiyası (y ekseni üzrə 180 dərəcə)

            visaTxt.isInvisible

            cardVisa.animate()
                .rotationY(90f) // ön tərəfi gizlədir
                .setDuration(200)
                .withEndAction {
                    // Flip tamamlanandan sonra yeni fragmentə keçid
                    val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
                        cardVisa to "card_transition"
                    )
                    findNavController().navigate(
                        R.id.action_nav_home_to_cardFragment,
                        null,
                        null,
                        extras
                    )
                }
                .start()
        }


    }

    private fun navigateToIncreaseBalance() {
        binding.appBar.findViewById<ImageView>(R.id.add_button).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_increaseBalanceFragment)
        }

        binding.appBar.findViewById<ImageView>(R.id.next_button).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_exportToFragment)
        }
    }


}
