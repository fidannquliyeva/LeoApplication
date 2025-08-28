package com.example.leoapplication.presentation.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentPinCreatedBinding
import com.example.leoapplication.presentation.viewmodel.PinCreatedVM
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class PinCreatedFragment : Fragment() {

    private lateinit var binding: FragmentPinCreatedBinding
    private val viewModel: PinCreatedVM by viewModels()
    private val enteredPin = StringBuilder()
    private val confirmPin = StringBuilder()
    private var isConfirmPhase = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPinCreatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNumberPad()
        observeViewModel()
        binding.helpPin.setOnClickListener { showSupportDialog() }

        viewModel.pinSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                val savedPin = viewModel.getSavedPin()
                println("✅ Saxlanmış PIN: $savedPin")  // Konsola yazdırır
                findNavController().navigate(R.id.action_pinCreatedFragment_to_pinLoginFragment)
            }
        }


    }


    private fun observeViewModel() {
        viewModel.pinSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(requireContext(), "PIN yadda saxlanıldı", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_pinCreatedFragment_to_pinLoginFragment)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupNumberPad() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        buttons.forEach { btn ->
            btn.setOnClickListener {
                if (!isConfirmPhase) {
                    if (enteredPin.length < 4) enteredPin.append(btn.text)
                    updatePinIndicators(binding.pin1, binding.pin2, binding.pin3, binding.pin4, enteredPin.length)
                    if (enteredPin.length == 4) startConfirmPhase()
                } else {
                    if (confirmPin.length < 4) confirmPin.append(btn.text)
                    updatePinIndicators(binding.pinC1, binding.pinC2, binding.pinC3, binding.pinC4, confirmPin.length)
                    if (confirmPin.length == 4) finishPinCreation()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            if (!isConfirmPhase) {
                if (enteredPin.isNotEmpty()) enteredPin.deleteCharAt(enteredPin.lastIndex)
                updatePinIndicators(binding.pin1, binding.pin2, binding.pin3, binding.pin4, enteredPin.length)
            } else {
                if (confirmPin.isNotEmpty()) confirmPin.deleteCharAt(confirmPin.lastIndex)
                updatePinIndicators(binding.pinC1, binding.pinC2, binding.pinC3, binding.pinC4, confirmPin.length)
            }
        }
    }

    private fun startConfirmPhase() {
        isConfirmPhase = true
        viewModel.setPin(enteredPin.toString())
    }

    private fun finishPinCreation() {
        viewModel.setConfirmPin(confirmPin.toString())
        viewModel.savePin()

        // Əgər səhv olduqda confirmPin-i reset et
        if (viewModel.pinSaved.value != true) {
            confirmPin.clear()
            updatePinIndicators(binding.pinC1, binding.pinC2, binding.pinC3, binding.pinC4, 0)
        }
    }

    private fun updatePinIndicators(p1: ImageView, p2: ImageView, p3: ImageView, p4: ImageView, filled: Int) {
        val pins = listOf(p1, p2, p3, p4)
        pins.forEachIndexed { index, imageView ->
            if (index < filled) {
                // Rəngi dəyiş və animasiya ver
                imageView.setColorFilter(resources.getColor(R.color.black))
                imageView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150).withEndAction {
                    imageView.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }.start()
            } else {
                imageView.setColorFilter(resources.getColor(R.color.colorUnchecked))
            }
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
