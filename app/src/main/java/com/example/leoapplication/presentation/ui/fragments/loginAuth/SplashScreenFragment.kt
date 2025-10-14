package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentSplashScreenBinding
import com.example.leoapplication.util.PinManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateTextWithScale("Leobank")
        goToNextScreen()
    }

    private fun goToNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("SplashScreen", "🔍 CHECKING AUTH & PIN...")

            val currentUser = auth.currentUser
            Log.d("SplashScreen", "Current user: ${currentUser?.uid ?: "NULL"}")

            if (currentUser == null) {
                // User logged out - PIN olsa belə login-ə göndər
                Log.d("SplashScreen", "❌ User NOT logged in → Going to LoginWithNumber")

                // Təhlükəsizlik üçün PIN-i də təmizlə
                if (PinManager.isPinSet(requireContext())) {
                    Log.d("SplashScreen", "⚠️ Cleaning orphaned PIN")
                    PinManager.clearPin(requireContext())
                }

                findNavController().navigate(R.id.action_splashScreenFragment_to_loginWithNumberFragment)
            } else {
                // User logged in
                Log.d("SplashScreen", "✅ User IS logged in: ${currentUser.uid}")

                val isPinSet = PinManager.isPinSet(requireContext())
                Log.d("SplashScreen", "Is PIN set: $isPinSet")

                if (isPinSet) {
                    Log.d("SplashScreen", "✅ PIN is SET → Going to PinLogin")
                    findNavController().navigate(R.id.action_splashScreenFragment_to_pinLoginFragment)
                } else {
                    Log.d("SplashScreen", "ℹ️ PIN NOT set → Going to Create PIN")
                    // PIN yaratma ekranına göndər (navigation action-unuza görə dəyişdirin)
                    findNavController().navigate(R.id.action_splashScreenFragment_to_setPinFragment)
                }
            }
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