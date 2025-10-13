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
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentMenuBinding
import com.example.leoapplication.util.LanguageManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
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

    private fun showSupportDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_support, null)
        dialog.setContentView(view)

        // Sosial media
        view.findViewById<LinearLayout>(R.id.layoutWhatsapp).setOnClickListener {
            openLink("https://wa.me/994123101488", "com.whatsapp")
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.layoutTelegram).setOnClickListener {
            openLink("https://t.me/Leobank_bot")
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.layoutFacebook).setOnClickListener {
            openLink("https://www.facebook.com/leobank.az/")
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.layoutViber).setOnClickListener {
            openLink("https://www.viber.com/leobank.az/")
            dialog.dismiss()
        }

        // ✅ Dil seçimi - LanguageManager ilə
        view.findViewById<ConstraintLayout>(R.id.layoutLanguage).setOnClickListener {
            dialog.dismiss()
            showLanguageDialog()
        }

        dialog.show()
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

    private fun openLink(url: String, packageName: String? = null) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (packageName != null) intent.setPackage(packageName)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tətbiq açılmadı", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}