package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentPinLoginBinding
import com.example.leoapplication.util.AvatarManager
import com.example.leoapplication.util.DialogHelper.showSupportDialog
import com.example.leoapplication.util.PinManager
import com.example.leoapplication.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinLoginFragment : Fragment() {

    private var _binding: FragmentPinLoginBinding? = null
    private val binding get() = _binding!!

    private var enteredPin = ""
    private val pinLength = 4
    private var lockTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLockStatus()
        setupNumberPad()
        setupForgetPin()
        loadAvatar()
        binding.helpPin.setOnClickListener {
            showSupportDialog()
        }
    }

    private fun checkLockStatus() {
        if (PinManager.isUserLocked(requireContext())) {
            disableInput()
            startLockTimer()
        } else {
            enableInput()
        }
    }

    private fun startLockTimer() {
        val remainingTime = PinManager.getRemainingLockTime(requireContext())

        lockTimer?.cancel()
        lockTimer = object : CountDownTimer(remainingTime * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val secs = seconds % 60
                binding.phoneNumberText.text =
                    "Gözləyin: ${minutes}:${secs.toString().padStart(2, '0')}"
            }

            override fun onFinish() {
                PinManager.resetFailedAttempts(requireContext())
                enableInput()
                showToast("Yenidən cəhd edə bilərsiniz")
                binding.phoneNumberText.text = "PIN kod"
            }
        }.start()
    }

    private fun disableInput() {
        binding.numberPad.alpha = 0.3f
        binding.numberPad.isEnabled = false

        //deaktiv
        listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btnDelete
        ).forEach { it.isEnabled = false }
    }

    private fun enableInput() {
        binding.numberPad.alpha = 1f
        binding.numberPad.isEnabled = true

        //aktiv
        listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btnDelete
        ).forEach { it.isEnabled = true }

        binding.phoneNumberText.text = "PIN kod"
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
        if (PinManager.isUserLocked(requireContext())) {
            showToast("Çox səhv cəhd etdiniz! Zəhmət olmasa gözləyin")
            return
        }

        if (enteredPin.length < pinLength) {
            enteredPin += digit
            updatePinDisplay()

            if (enteredPin.length == pinLength) {
                verifyPin()
            }
        }
    }

    private fun removeDigit() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            updatePinDisplay()
        }
    }

    private fun updatePinDisplay() {
        val pinViews = listOf(binding.pin1, binding.pin2, binding.pin3, binding.pin4)
        pinViews.forEachIndexed { index, imageView ->
            if (index < enteredPin.length) {
                imageView.setImageResource(R.drawable.paw)
                imageView.setColorFilter(
                    resources.getColor(android.R.color.holo_purple, null),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                imageView.setImageResource(R.drawable.paw)
                imageView.setColorFilter(
                    resources.getColor(android.R.color.darker_gray, null),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun verifyPin() {
        if (PinManager.verifyPin(requireContext(), enteredPin)) {
            PinManager.resetFailedAttempts(requireContext())
            showToast("Xoş gəldiniz!")
            findNavController().navigate(R.id.action_pinLoginFragment_to_homeFragment)
        } else {
            PinManager.addFailedAttempt(requireContext())
            val remainingAttempts = PinManager.getRemainingAttempts(requireContext())

            if (PinManager.isUserLocked(requireContext())) {
                showToast("Çox səhv cəhd! 5 dəqiqə gözləyin")
                disableInput()
                startLockTimer()
            } else {
                showToast("Yanlış PIN! Qalan cəhd: $remainingAttempts")
            }

            enteredPin = ""
            updatePinDisplay()
        }
    }

    private fun setupForgetPin() {
        binding.txtForget.setOnClickListener {
            showToast("Yenidən giriş edin")
            PinManager.clearPin(requireContext())
            findNavController().navigate(R.id.action_pinLogin_to_login)
        }
    }

    private fun loadAvatar() {
        val avatarPath = AvatarManager.getAvatar(requireContext())

        if (avatarPath != null) {
            try {
                val file = java.io.File(avatarPath)
                binding.imgAvatar.setImageURI(android.net.Uri.fromFile(file))
            } catch (e: Exception) {
                binding.imgAvatar.setImageResource(R.drawable.icons8testaccount80)
            }
        } else {
            binding.imgAvatar.setImageResource(R.drawable.icons8testaccount80)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lockTimer?.cancel()
        lockTimer = null
        _binding = null
    }
}