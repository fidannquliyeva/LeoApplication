package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentLoadingBinding
import com.example.leoapplication.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoadingFragment : Fragment() {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startProgressAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToHome()
        }, Constants.LOADING_DELAY)
    }

    private fun startProgressAnimation() {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = Constants.LOADING_DELAY

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            binding.progressBar.progress = progress
        }

        animator.start()
    }

    private fun navigateToHome() {
        val action = LoadingFragmentDirections.actionLoadingToHome()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}