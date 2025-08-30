package com.example.leoapplication.presentation.ui.fragments

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
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentLoginWithNumberBinding
import com.example.leoapplication.presentation.viewmodel.LoginWithNumberVM
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class LoginWithNumberFragment : Fragment() {

    private lateinit var binding: FragmentLoginWithNumberBinding
    private val viewModel: LoginWithNumberVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginWithNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        setupListeners()

        binding.infoTextNext.setOnClickListener {
            findNavController().navigate(R.id.action_loginWithNumberFragment_to_aboutLeoFragment)
        }
        binding.help.setOnClickListener { showSupportDialog() }
    }

    private fun setupUI() {
        binding.phoneNumberText.text = if (viewModel.phoneNumber.isEmpty()) "+994" else viewModel.phoneNumber
        setupNumberGrid()
    }

    private fun setupObservers() {
        viewModel.navigateToNewUser.observe(viewLifecycleOwner) { goToNewUser ->
            if (goToNewUser) {
                val action = LoginWithNumberFragmentDirections
                    .actionLoginWithNumberFragmentToNewUserInfoFragment(phone = viewModel.phoneNumber)
                findNavController().navigate(action)
            }
        }

        viewModel.navigateToSmsLogin.observe(viewLifecycleOwner) { goToSms ->
            if (goToSms) {
                val action = LoginWithNumberFragmentDirections
                    .actionLoginWithNumberFragmentToSmsLoginFragment(phoneNumber = viewModel.phoneNumber)
                findNavController().navigate(action)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }


        viewModel.bankCard.observe(viewLifecycleOwner) { card ->
            card?.let {
                // Burada kart məlumatını göstərə bilərsən (məs. dialog, fragment və ya RecyclerView)
                Toast.makeText(requireContext(), "Kart: ${it.cardNumber}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.nextButton.setOnClickListener {
            val inputNumber = binding.phoneNumberText.text.toString().replace(" ", "")
            if (inputNumber.length <= 12) {
                Toast.makeText(requireContext(), "Zəhmət olmasa nömrə daxil edin", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.phoneNumber = inputNumber
                viewModel.fetchUserOrRedirect() // user yoxlanır, kart yaradılır/əlavə olunur
            }
        }
        setupNumberGrid()
    }

    private fun setupNumberGrid() {
        val MAX_DIGITS = 9
        val numberClickListener = View.OnClickListener { view ->
            val button = view as Button
            val digit = button.text.toString()
            val text = binding.phoneNumberText.text.toString()
            val currentNumber = text.replace("+994", "").replace(" ", "")
            if (currentNumber.length < MAX_DIGITS) {
                binding.phoneNumberText.append(digit)
                viewModel.phoneNumber = binding.phoneNumberText.text.toString()
            }
        }

        listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        ).forEach { it.setOnClickListener(numberClickListener) }

        binding.btnDelete.setOnClickListener {
            val currentText = binding.phoneNumberText.text.toString()
            if (currentText.length > 4) {  // minimum "+994" qalır
                binding.phoneNumberText.text = currentText.dropLast(1)
                viewModel.phoneNumber = binding.phoneNumberText.text.toString()
            }
        }
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
