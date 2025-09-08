package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentSmsLoginBinding
import com.example.leoapplication.presentation.viewmodel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SmsLoginFragment : Fragment() {

    private lateinit var binding: FragmentSmsLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private val args: SmsLoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSmsLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumber()
        gridLayout()
        msgNotComing()
        binding.btnHelp.setOnClickListener { showSupportDialog() }



        setupObservers()

        // Test nömrəsi üçün Toast göstəririk, əks halda Firebase SMS göndərir
        if (args.phoneNumber in viewModel.testNumbers) {
            val testCode = viewModel.testNumbers[args.phoneNumber]
            Toast.makeText(requireContext(), "Test kod: $testCode", Toast.LENGTH_LONG).show()
        } else {
            viewModel.sendSms(args.phoneNumber)
        }
    }

        private fun setupObservers() {
        viewModel.smsSent.observe(viewLifecycleOwner) { sent ->
            if (sent == true) Toast.makeText(requireContext(), "SMS göndərildi", Toast.LENGTH_SHORT).show()
        }

        viewModel.verificationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) findNavController().navigate(R.id.action_smsLoginFragment_to_createPinFragment)
            else Toast.makeText(requireContext(), "Kod düzgün deyil", Toast.LENGTH_SHORT).show()
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun gridLayout() {
        val maxDigits = 6
        var currentIndex = 0
        viewModel.code = ""
        binding.msgNumber.text = "000000"  // başlanğıcda 6 sıfır

        binding.btnDelete.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                viewModel.code = viewModel.code.dropLast(1)
                val display = viewModel.code.padEnd(maxDigits, '0')  // boş yerləri sıfırla doldur
                binding.msgNumber.text = display
            }
        }

        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        val numberClickListener = View.OnClickListener { button ->
            val digit = (button as Button).text[0]
            if (currentIndex < maxDigits) {
                viewModel.code += digit
                currentIndex++
                val display = viewModel.code.padEnd(maxDigits, '0')  // boş yerləri sıfırla doldur
                binding.msgNumber.text = display
            }

            if (viewModel.code.length == maxDigits) {
                // test nömrəsi üçün yoxlama
                if (args.phoneNumber in viewModel.testNumbers) {
                    val testCode = viewModel.testNumbers[args.phoneNumber]
                    viewModel.verificationSuccess.value = viewModel.code == testCode
                } else {
                    // Firebase yoxlaması
                    viewModel.verifyCode(viewModel.code)
                }
            }
        }






        numberButtons.forEach { it.setOnClickListener(numberClickListener) }
    }


    private fun phoneNumber() {
        val phoneNumber = args.phoneNumber
        binding.txtMsg.text = phoneNumber
    }

    private fun msgNotComing() {
        binding.noMsgComing.setOnClickListener {
            Toast.makeText(requireContext(), "Yanlis nomre geri qayit", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_smsLoginFragment_to_loginWithNumberFragment)
        }
    }

    private fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", language).apply()
    }

    private fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("selected_language", "az") ?: "az"
    }

    private fun updateLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun applyLanguage(language: String) {
        saveLanguage(requireContext(), language)
        updateLocale(requireContext(), language)
        requireActivity().recreate()
    }

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
            val dialogLanguage = BottomSheetDialog(requireContext())
            val viewLanguage = layoutInflater.inflate(R.layout.dialog_language, null)
            dialogLanguage.setContentView(viewLanguage)

            val radioGroup = viewLanguage.findViewById<RadioGroup>(R.id.radioGroupLanguage)
            val radioAzerbaijan = viewLanguage.findViewById<RadioButton>(R.id.radioAzerbaijan)
            val radioRussian = viewLanguage.findViewById<RadioButton>(R.id.radioRussian)

            if (getSavedLanguage(requireContext()) == "az") radioGroup.check(R.id.radioAzerbaijan)
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
        dialog.show()
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
}
