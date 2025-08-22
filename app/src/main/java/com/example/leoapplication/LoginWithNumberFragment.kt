package com.example.leoapplication

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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentLoginWithNumberBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

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

        // Seçilmiş dili oxu və tətbiq et
        val currentLanguage = getSavedLanguage(requireContext())
        updateLocale(requireContext(), currentLanguage)

        goToAboutLeo()
        goToSmsLogin()
        gridLayout()
        refreshUI()

        binding.help.setOnClickListener { showSupportDialog() }
    }

    private fun refreshUI() {
        binding.infoTextNext.text = getString(R.string.r_li_d_ym_sini_s_xmaqla_siz)
        binding.nextButton.text = getString(R.string.r_li)
        binding.phoneNumberText.hint = getString(R.string._994)
        binding.help.text = getString(R.string.d_st_k)
    }

    private fun goToAboutLeo() {
        binding.infoTextNext.setOnClickListener {
            findNavController().navigate(R.id.action_loginWithNumberFragment_to_aboutLeoFragment)
        }
    }


    private fun goToSmsLogin() {
        binding.nextButton.setOnClickListener {
            val currentNumber = binding.phoneNumberText.text.toString()

            if (currentNumber.length <= 13) {
                Toast.makeText(requireContext(), "Zəhmət olmasa nömrə daxil edin", Toast.LENGTH_SHORT).show()
            } else {
                val action = LoginWithNumberFragmentDirections
                    .actionLoginWithNumberFragmentToSmsLoginFragment(phoneNumber = currentNumber)
                findNavController().navigate(action)
            }
        }
    }


    private fun gridLayout() {
        val MAX_DIGITS = 9
        val numberClickListener = View.OnClickListener { view ->
            val button = view as Button
            val digit = button.text.toString()
            val currentNumber = binding.phoneNumberText.text.toString().substring(5)
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
            if (currentText.length > 5) {
                binding.phoneNumberText.text = currentText.dropLast(1)
                viewModel.phoneNumber = binding.phoneNumberText.text.toString()
            }
        }

        binding.phoneNumberText.text = viewModel.phoneNumber.ifEmpty { "+994 " }
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
        requireActivity().recreate()  // Activity restart ilə bütün app UI yenilənir
    }

    private fun showSupportDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_support, null)
        dialog.setContentView(view)

        // Sosial media
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

        // Dil seçimi
        view.findViewById<ConstraintLayout>(R.id.layoutLanguage).setOnClickListener {
            dialog.dismiss()

            val dialogLanguage = BottomSheetDialog(requireContext())
            val viewLanguage = layoutInflater.inflate(R.layout.dialog_language, null)
            dialogLanguage.setContentView(viewLanguage)

            val radioGroup = viewLanguage.findViewById<RadioGroup>(R.id.radioGroupLanguage)
            val radioAzerbaijan = viewLanguage.findViewById<RadioButton>(R.id.radioAzerbaijan)
            val radioRussian = viewLanguage.findViewById<RadioButton>(R.id.radioRussian)

            // Seçilmiş dili göstər
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
