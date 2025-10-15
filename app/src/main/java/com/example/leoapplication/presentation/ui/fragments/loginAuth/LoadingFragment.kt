package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentLoadingBinding
import com.example.leoapplication.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingFragment : Fragment() {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!


    private var progressAnimator: ValueAnimator? = null
    private var handler: Handler? = null
    private var navigationRunnable: Runnable? = null

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
        scheduleNavigation()
    }

    private fun startProgressAnimation() {

        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = Constants.LOADING_DELAY

            addUpdateListener { animation ->
                _binding?.let {
                    val progress = animation.animatedValue as Int
                    it.progressBar?.progress = progress
                } ?: run {
                    animation.cancel()
                }
            }

            start()
        }
    }

    private fun scheduleNavigation() {

        handler = Handler(Looper.getMainLooper())

        navigationRunnable = Runnable {
            //fragment varsa
            if (isAdded && _binding != null) {
                navigateToSetPin()
            }
        }

        handler?.postDelayed(navigationRunnable!!, Constants.LOADING_DELAY)
    }

    private fun navigateToSetPin() {
        try {
            findNavController().navigate(R.id.action_loadingFragment_to_setPinFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        progressAnimator?.cancel()
        progressAnimator = null

        navigationRunnable?.let { handler?.removeCallbacks(it) }
        handler = null
        navigationRunnable = null

        _binding = null
    }
}