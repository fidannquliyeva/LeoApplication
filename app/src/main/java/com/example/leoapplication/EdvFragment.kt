package com.example.leoapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class EdvFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutEdv)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerEdv)
        val motionLayout = view.findViewById<MotionLayout>(R.id.motionLayoutEdv)
        val appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar_edv)

        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val progress = -verticalOffset / totalScrollRange.toFloat()
            motionLayout.progress = progress
        }

        val adapter = EdvPagersAdapter(this)
        viewPager.adapter = adapter

        val titles = listOf("Hamısı", "Gözləmədədir", "Qaytarılıb", "Ləğv edilib")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}