package com.example.leoapplication

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.leoapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                R.id.splashScreenFragment, R.id.smsLoginFragment, R.id.loginWithNumberFragment, R.id.pinLoginFragment , R.id.aboutLeoFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }
}