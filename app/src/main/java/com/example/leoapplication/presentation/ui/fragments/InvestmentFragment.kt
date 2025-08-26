package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.leoapplication.R
import com.example.leoapplication.presentation.ui.adapters.InvestmentsPagerAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvestmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_investment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val motionLayout = view.findViewById<MotionLayout>(R.id.motionLayoutInvestment)
        val appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar_investment)

        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val progress = -verticalOffset / totalScrollRange.toFloat()
            motionLayout.progress = progress
        }

        val adapter = InvestmentsPagerAdapter(this)
        viewPager.adapter = adapter

        val titles = listOf("Portfel", "Səhmlər", "Kripto", "Fondlar")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}