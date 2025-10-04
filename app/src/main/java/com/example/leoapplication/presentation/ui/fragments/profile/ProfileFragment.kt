package com.example.leoapplication.presentation.ui.fragments.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R

import com.example.leoapplication.databinding.FragmentProfileBinding


import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment() {
//
//    private lateinit var binding: FragmentProfileBinding
//    private val viewModel: ProfileVM by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentProfileBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
//        Log.d("ProfileFragment", "Current UID: $uid")
//        viewModel.loadUserProfile(uid)
//
//        observeProfile()
//
//        binding.labelTheme.setOnClickListener{showThemeDialog()}
//        binding.labelLang.setOnClickListener { showLanguageDialog() }
//        binding.btnLogout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            findNavController().navigate(R.id.action_profileFragment_to_loginWithNumberFragment)
//        }
//
//
//
//
//
//    }
//
//    private fun observeProfile() {
//        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
//            user?.let {
//                binding.valueName.text = it.firstName
//                binding.valueEmail.text = it.email
//                binding.valuePhone.text = it.phone
//                // avatar varsa Glide ilə yükləyə bilərsiniz
//                // Glide.with(this).load(it.avatarUrl).into(binding.imgAvatar)
//            }
//        }
//
//        viewModel.error.observe(viewLifecycleOwner) { msg ->
//            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
//        }
//    }
//
//
//
//
//
//    private fun showLanguageDialog() {
//        val dialogLanguage = BottomSheetDialog(requireContext())
//        val viewLanguage = layoutInflater.inflate(R.layout.dialog_language, null)
//        dialogLanguage.setContentView(viewLanguage)
//
//        val radioGroup = viewLanguage.findViewById<RadioGroup>(R.id.radioGroupLanguage)
//        val radioAzerbaijan = viewLanguage.findViewById<RadioButton>(R.id.radioAzerbaijan)
//        val radioRussian = viewLanguage.findViewById<RadioButton>(R.id.radioRussian)
//
//        // Seçilmiş dili göstər
//        if (getSavedLanguage(requireContext()) == "az") radioGroup.check(R.id.radioAzerbaijan)
//        else radioGroup.check(R.id.radioRussian)
//
//        radioAzerbaijan.setOnClickListener {
//            radioGroup.check(R.id.radioAzerbaijan)
//            applyLanguage("az")
//            dialogLanguage.dismiss()
//        }
//
//        radioRussian.setOnClickListener {
//            radioGroup.check(R.id.radioRussian)
//            applyLanguage("ru")
//            dialogLanguage.dismiss()
//        }
//
//        dialogLanguage.show()
//    }
//
//    private fun saveLanguage(context: Context, language: String) {
//        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        prefs.edit().putString("selected_language", language).apply()
//    }
//
//    private fun getSavedLanguage(context: Context): String {
//        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        return prefs.getString("selected_language", "az") ?: "az"
//    }
//
//    private fun updateLocale(context: Context, language: String) {
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//        val config = context.resources.configuration
//        config.setLocale(locale)
//        context.resources.updateConfiguration(config, context.resources.displayMetrics)
//    }
//
//    private fun applyLanguage(language: String) {
//        saveLanguage(requireContext(), language)
//        updateLocale(requireContext(), language)
//        requireActivity().recreate()  // Activity restart ilə UI yenilənir
//    }
//
//
//    private fun showThemeDialog() {
//        val themes = arrayOf("Açıq", "Tünd")
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Tema seçin")
//            .setSingleChoiceItems(themes, -1) { dialog, which ->
//                when (which) {
//                    0 -> {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                        Toast.makeText(requireContext(), "Açıq tema seçildi", Toast.LENGTH_SHORT).show()
//                    }
//                    1 -> {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                        Toast.makeText(requireContext(), "Tünd tema seçildi", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }

}


