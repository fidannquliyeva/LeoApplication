package com.example.leoapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            cardVisa.alpha = 1 - progress
            visaTxt.alpha = 1 - progress
            balanceNum.alpha = 1f
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
}
