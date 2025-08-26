package com.example.leoapplication.presentation.ui.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentPinLoginBinding
import com.example.leoapplication.presentation.viewmodel.PinLoginVM
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class PinLoginFragment : Fragment() {
    lateinit var binding: FragmentPinLoginBinding
    private val viewModel: PinLoginVM by viewModels()
    private val enteredPin = StringBuilder()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.helpPin.setOnClickListener {
        showSupportDialog()
        }
        setupNumberPad()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPinLoginBinding.inflate(inflater, container, false)
        return binding.root
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


    private fun setupNumberPad() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        // hər rəqəm düyməsi üçün
        buttons.forEach { button ->
            button.setOnClickListener {
                if (enteredPin.length < 4) {
                    enteredPin.append(button.text.toString())
                    updatePinIndicators()

                    if (enteredPin.length == 4) {
                        checkPin()
                    }
                }
            }
        }

        // sil düyməsi
        binding.btnDelete.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin.deleteCharAt(enteredPin.lastIndex)
                updatePinIndicators()
            }
        }
    }

    private fun updatePinIndicators() {
        val pins = listOf(binding.pin1, binding.pin2, binding.pin3, binding.pin4)

        pins.forEachIndexed { index, imageView ->
            if (index < enteredPin.length) {
                // rəng dəyişimi
                imageView.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.black),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )

                // böyütmə animasiyası
                val scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.5f, 1f)
                val scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.5f, 1f)
                AnimatorSet().apply {
                    playTogether(scaleX, scaleY)
                    duration = 200
                    start()
                }

            } else {
                imageView.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorUnchecked),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                // ölçünü normal et
                imageView.scaleX = 1f
                imageView.scaleY = 1f
            }
        }
    }

    private fun checkPin() {
        if (enteredPin.toString() == viewModel.correctCode) {

            findNavController().navigate(R.id.action_pinLoginFragment_to_homeFragment)
        } else {
            Toast.makeText(requireContext(), "Yanlış PIN", Toast.LENGTH_SHORT).show()
            enteredPin.clear()
            updatePinIndicators()
        }
    }
}


