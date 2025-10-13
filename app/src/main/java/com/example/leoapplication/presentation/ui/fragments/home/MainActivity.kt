package com.example.leoapplication.presentation.ui.fragments.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.ActivityMainBinding
import com.example.leoapplication.util.LanguageManager
import com.example.leoapplication.util.ThemeHelper
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ✅ YENİ - Notification permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "✅ Notification permission granted")
            getFCMToken()
        } else {
            Log.d("MainActivity", "❌ Notification permission denied")
            Toast.makeText(this, "Bildiriş icazəsi verilmədi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dil və tema yüklə
        LanguageManager.loadLanguage(this)
//        ThemeHelper.applyTheme(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        setupNavigation()

        // ✅ YENİ - Notification permission və FCM token
        requestNotificationPermission()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashScreenFragment,
                R.id.smsLoginFragment,
                R.id.loginWithNumberFragment,
                R.id.pinLoginFragment,
                R.id.aboutLeoFragment,
                R.id.increaseBalanceFragment,
                R.id.increaseWithGoogleFragment,
                R.id.increaseOtherBankFragment,
                R.id.increaseOtherCardFragment,
                R.id.increaseWithSwiftFragment,
                R.id.signUpFragment,
                R.id.paymentSuccessFragment,
                R.id.otherPaysFragment,
                R.id.exportToMobileFragment,
                R.id.monileNumberFragment,
                R.id.mobileBalanceFragment,
                R.id.loadingFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    // ✅ YENİ - Notification permission istə (Android 13+)
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission var
                    Log.d("MainActivity", "✅ Notification permission already granted")
                    getFCMToken()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // İzah göstər
                    Toast.makeText(
                        this,
                        "Bildirişlər üçün icazə lazımdır",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Permission istə
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 12 və aşağı - permission avtomatik verilir
            getFCMToken()
        }
    }

    // ✅ YENİ - FCM Token əldə et
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("MainActivity", "❌ FCM token failed: ${task.exception}")
                return@addOnCompleteListener
            }

            // FCM Token
            val token = task.result
            Log.d("MainActivity", "✅✅✅ FCM TOKEN ✅✅✅")
            Log.d("MainActivity", token)
            Log.d("MainActivity", "✅✅✅ FCM TOKEN ✅✅✅")

            // TODO: Token-u server-ə göndər (gələcəkdə)
            // sendTokenToServer(token)
        }
    }
}