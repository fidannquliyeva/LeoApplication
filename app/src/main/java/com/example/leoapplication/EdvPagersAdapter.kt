package com.example.leoapplication

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.leoapplication.edvPagersFragment.AllFragment
import com.example.leoapplication.edvPagersFragment.CancelledFragment
import com.example.leoapplication.edvPagersFragment.PendingFragment
import com.example.leoapplication.edvPagersFragment.RefundedFragment

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
