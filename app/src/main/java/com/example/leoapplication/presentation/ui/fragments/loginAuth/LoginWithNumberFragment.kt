package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentLoginWithNumberBinding
import com.example.leoapplication.presentation.viewmodel.PhoneAuthViewModel

import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
@AndroidEntryPoint
class LoginWithNumberFragment : Fragment() {

    private var _binding: FragmentLoginWithNumberBinding? = null
    private val binding get() = _binding!!

    private val phoneAuthViewModel: PhoneAuthViewModel by viewModels()

    private val phoneBuilder = StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginWithNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.help.setOnClickListener{
            showSupportDialog()
        }
        setupNumberPad()
        setupObservers()

        binding.nextButton.setOnClickListener {
            val phoneNumber = "+994" + phoneBuilder.toString()
            if (phoneBuilder.length == 9) {
                phoneAuthViewModel.checkNumberAndSendOtp(phoneNumber, requireActivity())
            } else {
                Toast.makeText(requireContext(), "Düzgün nömrə daxil edin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNumberPad() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (phoneBuilder.length < 9) { // +994 daxil deyil, sadəcə son 9 rəqəm
                    phoneBuilder.append(index)
                    updatePhoneText()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            if (phoneBuilder.isNotEmpty()) {
                phoneBuilder.deleteCharAt(phoneBuilder.length - 1)
                updatePhoneText()
            }
        }
    }

    private fun updatePhoneText() {
        binding.phoneNumberText.text = "+994${phoneBuilder}"
    }

    private fun setupObservers() {
        phoneAuthViewModel.otpSent.observe(viewLifecycleOwner) { verificationId ->
            if (!verificationId.isNullOrEmpty()) {
                val action = LoginWithNumberFragmentDirections
                    .actionLoginWithNumberFragmentToSmsLoginFragment(verificationId)
                findNavController().navigate(action)
            }
        }

        phoneAuthViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Support / sosial media
    private fun showSupportDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_support, null)
        dialog.setContentView(view)

        view.findViewById<LinearLayout>(R.id.layoutWhatsapp).setOnClickListener {
            openLink("https://wa.me/994123101488", "com.whatsapp"); dialog.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.layoutTelegram).setOnClickListener {
            openLink("https://t.me/Leobank_bot"); dialog.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.layoutFacebook).setOnClickListener {
            openLink("https://www.facebook.com/leobank.az/"); dialog.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.layoutViber).setOnClickListener {
            openLink("https://www.viber.com/leobank.az/"); dialog.dismiss()
        }

        view.findViewById<ConstraintLayout>(R.id.layoutLanguage).setOnClickListener {
            dialog.dismiss()
            showLanguageDialog()
        }

        dialog.show()
    }

    private fun showLanguageDialog() {
        val dialogLanguage = BottomSheetDialog(requireContext())
        val viewLanguage = layoutInflater.inflate(R.layout.dialog_language, null)
        dialogLanguage.setContentView(viewLanguage)

        val radioGroup = viewLanguage.findViewById<RadioGroup>(R.id.radioGroupLanguage)
        val radioAzerbaijan = viewLanguage.findViewById<RadioButton>(R.id.radioAzerbaijan)
        val radioRussian = viewLanguage.findViewById<RadioButton>(R.id.radioRussian)

        if (getSavedLanguage() == "az") radioGroup.check(R.id.radioAzerbaijan)
        else radioGroup.check(R.id.radioRussian)

        radioAzerbaijan.setOnClickListener {
            radioGroup.check(R.id.radioAzerbaijan)
            applyLanguage("az")
            dialogLanguage.dismiss()
        }

        radioRussian.setOnClickListener {
            radioGroup.check(R.id.radioRussian)
            applyLanguage("ru")
            dialogLanguage.dismiss()
        }

        dialogLanguage.show()
    }

    private fun openLink(url: String, packageName: String? = null) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
            if (packageName != null) intent.setPackage(packageName)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tətbiq açılmadı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSavedLanguage(): String {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("selected_language", "az") ?: "az"
    }

    private fun saveLanguage(language: String) {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", language).apply()
    }

    private fun applyLanguage(language: String) {
        saveLanguage(language)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        requireActivity().recreate()
    }
}
