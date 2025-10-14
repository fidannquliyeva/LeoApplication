package com.example.leoapplication.presentation.ui.fragments.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentMenuBinding
import com.example.leoapplication.presentation.viewmodel.HomeViewModel
import com.example.leoapplication.util.DialogHelper.showSupportDialog
import com.example.leoapplication.util.LanguageManager
import com.example.leoapplication.util.openLink
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObservers()


    }

    private fun setupClickListeners() {
        // Profile
        binding.txtAccountMenu.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_profileFragment)
        }

        // Home
        binding.cardMain.setOnClickListener {

            findNavController().navigate(R.id.action_nav_menu_to_nav_home)
        }

        // Currency
        binding.txtCurrencyMenu.setOnClickListener {
            CurrencyDialogFragment().show(parentFragmentManager, "currencyDialog")
        }

        // Support
        binding.txtHelpMenu.setOnClickListener {
            showSupportDialog()
        }

        // Instagram
        binding.txtGMenu.setOnClickListener {
            openLink("https://www.instagram.com/leobank.az/")
        }

    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                homeViewModel.balance.collect { balance ->
                    binding.txtMainNum.text = String.format("%.2f ₼", balance)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}