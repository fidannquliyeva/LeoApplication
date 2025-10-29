package com.example.leoapplication

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

    private var backgroundTime: Long = 0
    private val PIN_TIMEOUT_MS = 10_000L

    // Notification permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getFCMToken()
        } else {
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

    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
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
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("MainActivity", "✅✅✅ FCM TOKEN ✅✅✅")
            Log.d("MainActivity", token)
            Log.d("MainActivity", "✅✅✅ FCM TOKEN ✅✅✅")
        }
    }


    override fun onPause() {
        super.onPause()

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val hasPIN = PinManager.isPinSet(this)

        if (isLoggedIn && hasPIN) {
            backgroundTime = System.currentTimeMillis()
            Log.d("MainActivity", "⏸️ App paused at: $backgroundTime")
        }
    }

    override fun onResume() {
        super.onResume()


        if (backgroundTime > 0) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - backgroundTime



            if (elapsedTime > PIN_TIMEOUT_MS) {
                Log.d("MainActivity", "🔒 Timeout! Redirecting to PIN login...")
                navigateToPinLogin()
            }

            // Vaxtı sıfırla
            backgroundTime = 0
        }
    }


    private fun navigateToPinLogin() {
        try {
            if (!::navController.isInitialized) {
                return
            }

            val currentDest = navController.currentDestination?.id


            if (currentDest == R.id.pinLoginFragment ||
                currentDest == R.id.loginWithNumberFragment ||
                currentDest == R.id.smsLoginFragment ||
                currentDest == R.id.splashScreenFragment) {
                Log.d("MainActivity", "Already on login screen, skipping navigation")
                return
            }


            navController.navigate(
                R.id.pinLoginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestinationId, true)
                    .setLaunchSingleTop(true)
                    .build()
            )


        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Navigation error: ${e.message}")
        }
    }
}