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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.ActivityMainBinding
import com.example.leoapplication.util.LanguageManager
import com.example.leoapplication.util.PinManager
import com.example.leoapplication.util.ThemeHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // ✅ YENİ: Background vaxtını saxlamaq üçün
    private var backgroundTime: Long = 0
    private val PIN_TIMEOUT_MS = 10_000L // 10 saniyə (millisaniyə ilə)

    // Notification permission launcher
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

        LanguageManager.loadLanguage(this)
        ThemeHelper.applyTheme(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        setupNavigation()
        requestNotificationPermission()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

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
                R.id.loadingFragment,
                R.id.setPinFragment,
                R.id.transactionDetailFragment,
                R.id.transferSelectFragment,
                R.id.profileFragment,
                R.id.transferManualInputFragment,
                R.id.transferAmountFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }

        Log.d("MainActivity", "Navigation setup complete")
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "✅ Notification permission already granted")
                    getFCMToken()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(
                        this,
                        "Bildirişlər üçün icazə lazımdır",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            getFCMToken()
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("MainActivity", "❌ FCM token failed: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("MainActivity", "✅✅✅ FCM TOKEN ✅✅✅")
            Log.d("MainActivity", token)
            Log.d("MainActivity", "✅✅✅ FCM TOKEN ✅✅✅")
        }
    }

    // ✅ YENİ: App background-a getdikdə
    override fun onPause() {
        super.onPause()

        // Yalnız user login olubsa və PIN varsa vaxtı qeyd et
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val hasPIN = PinManager.isPinSet(this)

        if (isLoggedIn && hasPIN) {
            backgroundTime = System.currentTimeMillis()
            Log.d("MainActivity", "⏸️ App paused at: $backgroundTime")
        }
    }

    // ✅ YENİ: App foreground-a qayıdanda
    override fun onResume() {
        super.onResume()

        // Əgər background vaxtı qeyd olunubsa
        if (backgroundTime > 0) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - backgroundTime

            Log.d("MainActivity", "▶️ App resumed. Elapsed time: ${elapsedTime}ms")

            // Əgər 10 saniyədən çox keçibsə
            if (elapsedTime > PIN_TIMEOUT_MS) {
                Log.d("MainActivity", "🔒 Timeout! Redirecting to PIN login...")
                navigateToPinLogin()
            }

            // Vaxtı sıfırla
            backgroundTime = 0
        }
    }

    // ✅ YENİ: PIN login ekranına yönləndirmə
    private fun navigateToPinLogin() {
        try {
            // NavController hazır olana qədər gözlə
            if (!::navController.isInitialized) {
                Log.e("MainActivity", "NavController not initialized yet")
                return
            }

            val currentDest = navController.currentDestination?.id

            // Əgər artıq PIN login ekranındaysa və ya login ekranlarındaysa, yönləndirmə
            if (currentDest == R.id.pinLoginFragment ||
                currentDest == R.id.loginWithNumberFragment ||
                currentDest == R.id.smsLoginFragment ||
                currentDest == R.id.splashScreenFragment) {
                Log.d("MainActivity", "Already on login screen, skipping navigation")
                return
            }

            // PIN login ekranına get və bütün stack-i təmizlə
            navController.navigate(
                R.id.pinLoginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestinationId, true)
                    .setLaunchSingleTop(true)
                    .build()
            )

            Log.d("MainActivity", "✅ Navigated to PIN login")

        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Navigation error: ${e.message}")
        }
    }
}