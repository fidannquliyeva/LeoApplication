package com.example.leoapplication.presentation.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentProfileBinding
import com.example.leoapplication.presentation.viewmodel.ProfileViewModel
import com.example.leoapplication.presentation.viewmodel.ProfileUiState
import com.example.leoapplication.util.LanguageManager
import com.example.leoapplication.util.ThemeHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        setupTheme()
        setupLanguage()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userData.collect { user ->
                        user?.let {
                            binding.valueName.text = it.fullName
                            binding.valuePhone.text = it.phoneNumber
                            binding.valueEmail.text = it.email.ifEmpty { "Email yoxdur" }
                        }
                    }
                }

                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is ProfileUiState.Loading -> {}
                            is ProfileUiState.Success -> {}
                            is ProfileUiState.LoggedOut -> {
                                navigateToLogin()
                            }
                            is ProfileUiState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEdit.setOnClickListener {
            Toast.makeText(requireContext(), "Avatar dəyişmə tezliklə", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.valueLang.setOnClickListener {
            showLanguageDialog()
        }

        binding.valueTheme.setOnClickListener {
            toggleTheme()
        }

        binding.switchBio.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(), if (isChecked) "Biometrik aktiv" else "Biometrik deaktiv", Toast.LENGTH_SHORT).show()
        }

        binding.switchOtherUser.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(), if (isChecked) "Məxfilik aktiv" else "Məxfilik deaktiv", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTheme() {
        val isDark = viewModel.isDarkTheme()
        updateThemeUI(isDark)
    }

    private fun setupLanguage() {
        val currentLang = LanguageManager.getSavedLanguage(requireContext())
        updateLanguageUI(currentLang)
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Azərbaycan", "English", "Русский")
        val languageCodes = arrayOf("az", "en", "ru")

        val currentLang = LanguageManager.getSavedLanguage(requireContext())
        val checkedItem = languageCodes.indexOf(currentLang).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(requireContext())
            .setTitle("Dil seçin")
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                val selectedLang = languageCodes[which]
                if (selectedLang != currentLang) {
                    LanguageManager.applyLanguage(requireActivity(), selectedLang)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Ləğv et", null)
            .show()
    }

    private fun updateLanguageUI(langCode: String) {
        binding.valueLang.text = when (langCode) {
            "az" -> "Azərbaycan dili"
            "en" -> "English"
            "ru" -> "Русский"
            else -> "Azərbaycan dili"
        }
    }

    private fun toggleTheme() {
        val newTheme = !viewModel.isDarkTheme()
        viewModel.saveTheme(newTheme)
        updateThemeUI(newTheme)

        Toast.makeText(requireContext(), if (newTheme) "Qaranlıq tema" else "İşıqlı tema", Toast.LENGTH_SHORT).show()

        requireActivity().recreate()
    }

    private fun updateThemeUI(isDark: Boolean) {
        binding.valueTheme.text = if (isDark) "Qaranlıq" else "İşıqlı"
    }

    private fun navigateToLogin() {
        try {
            findNavController().navigate(
                R.id.loginWithNumberFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(0, true)
                    .build()
            )
        } catch (e: Exception) {

            val intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}