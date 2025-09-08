package com.example.leoapplication.presentation.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.leoapplication.presentation.ui.fragments.investment.CryptoFragment
import com.example.leoapplication.presentation.ui.fragments.investment.FundsFragment
import com.example.leoapplication.presentation.ui.fragments.investment.PortfolioFragment
import com.example.leoapplication.presentation.ui.fragments.investment.StocksFragment

class InvestmentsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PortfolioFragment()
            1 -> StocksFragment()
            2 -> CryptoFragment()
            else -> FundsFragment()
        }
    }
}
