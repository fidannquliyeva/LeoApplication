package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentSetPinBinding
import com.example.leoapplication.util.DialogHelper.showSupportDialog
import com.example.leoapplication.util.PinManager
import com.example.leoapplication.util.showToast
//import com.example.leoapplication.databinding.FragmentPinCreatedBinding
//import com.example.leoapplication.presentation.viewmodel.PinCreatedVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetPinFragment : Fragment() {

    private var _binding: FragmentSetPinBinding? = null
    private val binding get() = _binding!!

    private var firstPin = ""
    private var confirmPin = ""
    private var isConfirming = false
    private var isPinVisible = false
    private var isConfirmPinVisible = false

    private val pinLength = 4

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPad()
        setupEyeToggle()
        updateInstructions()
        binding.helpPin.setOnClickListener {
            showSupportDialog()
        }
    }

    private fun setupNumberPad() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                addDigit(if (index == 0) "0" else index.toString())
            }
        }

        binding.btnDelete.setOnClickListener {
            removeDigit()
        }
    }

    private fun addDigit(digit: String) {
        if (!isConfirming) {
            // İlk PIN daxil edilir
            if (firstPin.length < pinLength) {
                firstPin += digit
                updatePinDisplay()

                if (firstPin.length == pinLength) {
                    // PIN-in gücünü yoxla
                    if (!isPinStrong(firstPin)) {
                        showToast("Zəhmət olmasa daha güclü PIN seçin (ardıcıl və ya təkrar rəqəmlər tövsiyə olunmur)")
                    }
                    // İlk PIN tamamlandı, təsdiq mərhələsinə keç
                    isConfirming = true
                    updateInstructions()
                    showToast("İndi PIN-i təsdiqləyin")
                }
            }
        } else {
            // Təsdiq PIN daxil edilir
            if (confirmPin.length < pinLength) {
                confirmPin += digit
                updatePinDisplay()

                if (confirmPin.length == pinLength) {
                    // Təsdiq PIN tamamlandı, yoxla
                    verifyPins()
                }
            }
        }
    }

    private fun removeDigit() {
        if (!isConfirming) {
            if (firstPin.isNotEmpty()) {
                firstPin = firstPin.dropLast(1)
                updatePinDisplay()
            }
        } else {
            if (confirmPin.isNotEmpty()) {
                confirmPin = confirmPin.dropLast(1)
                updatePinDisplay()
            }
        }
    }

    private fun updatePinDisplay() {
        // İlk PIN göstərmə
        val pinViews = listOf(binding.pin1, binding.pin2, binding.pin3, binding.pin4)
        pinViews.forEachIndexed { index, imageView ->
            when {
                index < firstPin.length -> {
                    if (isPinVisible && firstPin.isNotEmpty()) {
                        imageView.setImageResource(R.drawable.paw)
                    } else {
                        imageView.setImageResource(R.drawable.paw)
                    }
                }
                else -> {
                    imageView.setImageResource(R.drawable.paw)
                }
            }
        }

        // Təsdiq PIN göstərmə
        val confirmPinViews = listOf(binding.pinC1, binding.pinC2, binding.pinC3, binding.pinC4)
        confirmPinViews.forEachIndexed { index, imageView ->
            when {
                index < confirmPin.length -> {
                    if (isConfirmPinVisible && confirmPin.isNotEmpty()) {
                        imageView.setImageResource(R.drawable.paw)
                    } else {
                        imageView.setImageResource(R.drawable.paw)
                    }
                }
                else -> {
                    imageView.setImageResource(R.drawable.paw)
                }
            }
        }
    }

    private fun setupEyeToggle() {
        // İlk PIN göz icon
        binding.eyeToggle.setOnClickListener {
            isPinVisible = !isPinVisible
            binding.eyeToggle.setImageResource(
                if (isPinVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
            )
            updatePinDisplay()
        }

        // Təsdiq PIN göz icon
        binding.eyeToggleConfirm.setOnClickListener {
            isConfirmPinVisible = !isConfirmPinVisible
            binding.eyeToggleConfirm.setImageResource(
                if (isConfirmPinVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
            )
            updatePinDisplay()
        }
    }

    private fun updateInstructions() {
        // UI-da instruction text varsa, onu yenilə
        binding.confirmNumberText?.text = if (isConfirming) {
            "PIN-i təsdiqləyin"
        } else {
            "4 rəqəmli PIN təyin edin"
        }
    }

    private fun isPinStrong(pin: String): Boolean {
        // Ardıcıl rəqəmləri yoxla (1234, 4321)
        val isSequential = pin.zipWithNext().all { (a, b) -> b.code - a.code == 1 } ||
                pin.zipWithNext().all { (a, b) -> a.code - b.code == 1 }

        // Eyni rəqəmləri yoxla (1111)
        val allSame = pin.all { it == pin[0] }

        return !isSequential && !allSame
    }

    private fun verifyPins() {
        if (firstPin == confirmPin) {
            // PIN-lər uyğundur, saxla
            PinManager.savePin(requireContext(), firstPin)
            showToast("PIN uğurla təyin edildi! İndi PIN ilə daxil olun")

            // PinLogin-a keçid
            findNavController().navigate(R.id.action_pinCreatedFragment_to_pinLoginFragment)
        } else {
            // PIN-lər uyğun deyil
            showToast("PIN kodları uyğun gəlmir! Yenidən cəhd edin")
            resetPins()
        }
    }

    private fun resetPins() {
        firstPin = ""
        confirmPin = ""
        isConfirming = false
        updatePinDisplay()
        updateInstructions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}