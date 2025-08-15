package com.example.leoapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

class CollectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val motionLayout = view.findViewById<MotionLayout>(R.id.motionLayoutCollect)
        val scrollView = view.findViewById<AppBarLayout>(R.id.app_bar_collect)

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY.toFloat()
            val maxScroll = 200f
            val progress = (scrollY / maxScroll).coerceIn(0f, 1f)
            motionLayout.progress = progress
        }
    }
}

