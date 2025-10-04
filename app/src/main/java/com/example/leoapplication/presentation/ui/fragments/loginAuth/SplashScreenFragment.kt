package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentSplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateTextWithScale("Leobank")
        goToSign()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
       return binding.root
    }


    private fun goToSign(){
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashScreenFragment_to_newUserInfoFragment)
        }, 2000)

    }


    private fun animateTextWithScale(text: String) {
        binding.txtSplashScreen.text = ""
        lifecycleScope.launch {
            for (i in text.indices) {
                binding.txtSplashScreen.text = text.substring(0, i + 1)

                binding.txtSplashScreen.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(200)
                    .withEndAction {
                        binding.txtSplashScreen.scaleX = 1f
                        binding.txtSplashScreen.scaleY = 1f
                    }

                delay(120)
            }
        }
    }


}