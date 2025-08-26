package com.example.leoapplication.presentation.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.leoapplication.presentation.ui.fragments.AllFragment
import com.example.leoapplication.presentation.ui.fragments.CancelledFragment
import com.example.leoapplication.presentation.ui.fragments.PendingFragment
import com.example.leoapplication.presentation.ui.fragments.RefundedFragment

class EdvPagersAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllFragment()
            1 -> PendingFragment()
            2 -> RefundedFragment()
            else -> CancelledFragment()
        }
    }
}
