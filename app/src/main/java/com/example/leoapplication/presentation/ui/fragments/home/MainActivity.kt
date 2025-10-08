package com.example.leoapplication.presentation.ui.fragments.home

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.ActivityMainBinding
import com.example.leoapplication.util.LanguageManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageManager.loadLanguage(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashScreenFragment, R.id.smsLoginFragment, R.id.loginWithNumberFragment, R.id.pinLoginFragment, R.id.aboutLeoFragment, R.id.increaseBalanceFragment, R.id.increaseWithGoogleFragment, R.id.increaseOtherBankFragment, R.id.increaseOtherCardFragment,
                R.id.increaseWithSwiftFragment,  R.id.pinLoginFragment , R.id.signUpFragment ,
                       R.id.paymentSuccessFragment,
                         R.id.otherPaysFragment,R.id.exportToMobileFragment,R.id.monileNumberFragment,
                                    R.id.mobileBalanceFragment,R.id.loadingFragment
                                -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }
}