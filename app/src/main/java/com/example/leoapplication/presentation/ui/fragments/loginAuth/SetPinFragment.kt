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
        updatePinDisplay()

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
                    if (!isPinStrong(firstPin)) {
                        showToast("Zəhmət olmasa daha güclü PIN seçin (ardıcıl və ya təkrar rəqəmlər tövsiyə olunmur)")
                    }
                    //kec digeer
                    isConfirming = true
                    updateInstructions()
                    showToast("İndi PIN-i təsdiqləyin")
                }
            }
        } else {

            if (confirmPin.length < pinLength) {
                confirmPin += digit
                updatePinDisplay()

                if (confirmPin.length == pinLength) {
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
        val pinTexts = listOf(binding.pin1Text, binding.pin2Text, binding.pin3Text, binding.pin4Text)

        pinViews.forEachIndexed { index, imageView ->
            if (index < firstPin.length) {
                // Dolu - qara rəng
                imageView.setImageResource(R.drawable.paw)
                imageView.setColorFilter(
                    resources.getColor(android.R.color.black, null),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )

                // Rəqəmi göstər/gizlət
                if (isPinVisible) {
                    imageView.visibility = View.GONE
                    pinTexts[index].text = firstPin[index].toString()
                    pinTexts[index].visibility = View.VISIBLE
                } else {
                    imageView.visibility = View.VISIBLE
                    pinTexts[index].visibility = View.GONE
                }
            } else {
                // Boş - boz rəng
                imageView.setImageResource(R.drawable.paw)
                imageView.setColorFilter(
                    resources.getColor(android.R.color.darker_gray, null),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                imageView.visibility = View.VISIBLE
                pinTexts[index].visibility = View.GONE
            }
        }

        // Təsdiq PIN göstərmə
        val confirmPinViews = listOf(binding.pinC1, binding.pinC2, binding.pinC3, binding.pinC4)
        val confirmPinTexts = listOf(binding.pinC1Text, binding.pinC2Text, binding.pinC3Text, binding.pinC4Text)

        confirmPinViews.forEachIndexed { index, imageView ->
            if (index < confirmPin.length) {
                // Dolu - qara rəng
                imageView.setImageResource(R.drawable.paw)
                imageView.setColorFilter(
                    resources.getColor(android.R.color.black, null),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )

                // Rəqəmi göstər/gizlət
                if (isConfirmPinVisible) {
                    imageView.visibility = View.GONE
                    confirmPinTexts[index].text = confirmPin[index].toString()
                    confirmPinTexts[index].visibility = View.VISIBLE
                } else {
                    imageView.visibility = View.VISIBLE
                    confirmPinTexts[index].visibility = View.GONE
                }
            } else {
                // Boş - boz rəng
                imageView.setImageResource(R.drawable.paw)
                imageView.setColorFilter(
                    resources.getColor(android.R.color.darker_gray, null),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                imageView.visibility = View.VISIBLE
                confirmPinTexts[index].visibility = View.GONE
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
        binding.phoneNumberText.text = if (!isConfirming) {
            "Yeni PIN kodunuzu yaradın"
        } else {
            "PIN kodunuzu təsdiqləyin"
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
            PinManager.savePin(requireContext(), firstPin)
            showToast("PIN uğurla təyin edildi! İndi PIN ilə daxil olun")

            findNavController().navigate(R.id.action_pinCreatedFragment_to_pinLoginFragment)
        } else {

            showToast("PIN kodları uyğun gəlmir! Yenidən cəhd edin")
            resetPins()
        }
    }

    private fun resetPins() {
        firstPin = ""
        confirmPin = ""
        isConfirming = false
        isPinVisible = false
        isConfirmPinVisible = false
        binding.eyeToggle.setImageResource(R.drawable.ic_eye_closed)
        binding.eyeToggleConfirm.setImageResource(R.drawable.ic_eye_closed)
        updatePinDisplay()
        updateInstructions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}