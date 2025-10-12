package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentLoginWithNumberBinding
import com.example.leoapplication.presentation.viewmodel.PhoneAuthViewModel
import com.example.leoapplication.util.Constants
import com.example.leoapplication.util.DialogHelper.showSupportDialog
import com.example.leoapplication.util.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginWithNumberFragment : Fragment() {

    private var _binding: FragmentLoginWithNumberBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneAuthViewModel by activityViewModels()

    private var phoneNumber = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginWithNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPad()
        setupNextButton()
        binding.help.setOnClickListener {
            showSupportDialog()
        }
        binding.infoTextNext.setOnClickListener{
            findNavController().navigate(R.id.action_loginWithNumberFragment_to_aboutLeoFragment)
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
                if (phoneNumber.length < Constants.PHONE_NUMBER_LENGTH) {
                    phoneNumber += if (index == 0) "0" else index.toString()
                    updatePhoneDisplay()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            if (phoneNumber.isNotEmpty()) {
                phoneNumber = phoneNumber.dropLast(1)
                updatePhoneDisplay()
            }
        }
    }

    private fun updatePhoneDisplay() {
        val formattedNumber = buildString {
            append(Constants.COUNTRY_CODE)
            if (phoneNumber.isNotEmpty()) {
                append(" ")
                phoneNumber.forEachIndexed { index, char ->
                    append(char)
                    if ((index + 1) % 3 == 0 && index != phoneNumber.length - 1) {
                        append(" ")
                    }
                }
            }
        }

        binding.phoneNumberText.text = formattedNumber
        binding.nextButton.isEnabled = phoneNumber.length == Constants.PHONE_NUMBER_LENGTH
    }
    private fun setupNextButton() {
        binding.nextButton.isEnabled = false

        binding.nextButton.setOnClickListener {
            if (phoneNumber.length == Constants.PHONE_NUMBER_LENGTH) {
                // ✅ Boşluqları təmizlə
                val cleanNumber = phoneNumber.replace(" ", "")
                val fullPhoneNumber = "${Constants.COUNTRY_CODE}$cleanNumber"

                // ✅ Log (debug üçün)
                android.util.Log.d("LoginFragment", "📱 Göndərilən nömrə: $fullPhoneNumber")

                viewModel.sendVerificationCode(fullPhoneNumber, requireActivity())

                val action = LoginWithNumberFragmentDirections
                    .actionLoginWithNumberFragmentToSmsLoginFragment(fullPhoneNumber)
                findNavController().navigate(action)
            } else {
                showToast("Zəhmət olmasa 9 rəqəmli nömrə daxil edin")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


