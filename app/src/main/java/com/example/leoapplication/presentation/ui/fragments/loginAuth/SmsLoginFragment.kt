package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentSmsLoginBinding
import com.example.leoapplication.presentation.viewmodel.PhoneAuthViewModel
import com.example.leoapplication.util.Constants
import com.example.leoapplication.util.DialogHelper.showSupportDialog
import com.example.leoapplication.util.PinManager
import com.example.leoapplication.util.Resource
import com.example.leoapplication.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SmsLoginFragment : Fragment() {

    private var _binding: FragmentSmsLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneAuthViewModel by activityViewModels()
    private val args: SmsLoginFragmentArgs by navArgs()

    private var otpCode = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmsLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupNumberPad()
        observeViewModel()
        binding.btnHelp.setOnClickListener {
            showSupportDialog()
        }

    }

    private fun setupUI() {
        binding.txtMsg.text = args.phoneNumber
        updateOtpDisplay()
    }

    private fun setupNumberPad() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (otpCode.length < Constants.OTP_LENGTH) {
                    otpCode += if (index == 0) "0" else index.toString()
                    updateOtpDisplay()

                    if (otpCode.length == Constants.OTP_LENGTH) {
                        verifyOtp()
                    }
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            if (otpCode.isNotEmpty()) {
                otpCode = otpCode.dropLast(1)
                updateOtpDisplay()
            }
        }

        binding.infoMsg2.setOnClickListener {
            viewModel.sendVerificationCode(args.phoneNumber, requireActivity())
            showToast("Kod yenidən göndərildi")
        }
    }

    private fun updateOtpDisplay() {
        val displayText = buildString {
            for (i in 0 until Constants.OTP_LENGTH) {
                if (i < otpCode.length) {
                    append(otpCode[i])
                } else {
                    append("0")
                }
                if (i < Constants.OTP_LENGTH - 1) {
                    append(" ")
                }
            }
        }

        binding.msgNumber.text = displayText
        binding.msgNumber.setTextColor(
            if (otpCode.length == Constants.OTP_LENGTH) {
                resources.getColor(R.color.textColor, null)
            } else {
                resources.getColor(android.R.color.darker_gray, null)
            }
        )
    }

    private fun verifyOtp() {
        viewModel.verifyCode(otpCode)
    }

    private fun observeViewModel() {
        viewModel.verificationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PhoneAuthViewModel.VerificationState.CodeSending -> {
                    // Loading
                }
                is PhoneAuthViewModel.VerificationState.CodeSent -> {
                    showToast("Kod göndərildi")
                }
                is PhoneAuthViewModel.VerificationState.CodeSendFailed -> {
                    showToast(state.message, android.widget.Toast.LENGTH_LONG)
                }
            }
        }

        viewModel.authState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.msgNumber.text = "Yoxlanılır..."
                }
                is Resource.Success -> {
                    val userId = resource.data!!


                    checkUserExists(userId)
                }
                is Resource.Error -> {
                    showToast(resource.message ?: "Xəta baş verdi", android.widget.Toast.LENGTH_LONG)
                    otpCode = ""
                    updateOtpDisplay()
                }
            }
        }
    }

    private fun checkUserExists(userId: String) {
        viewLifecycleOwner.lifecycleScope.launch {

            val userExists = viewModel.checkIfUserExists(userId)

            if (userExists) {
                checkPinAndNavigate()
            } else {

                val action = SmsLoginFragmentDirections
                    .actionSmsLoginToSignUp(userId, args.phoneNumber)
                findNavController().navigate(action)
            }
        }
    }


    private fun checkPinAndNavigate() {
        if (PinManager.isPinSet(requireContext())) {
            showToast("PIN ilə daxil olun")
            findNavController().navigate(R.id.action_smsLogin_to_pinLogin)
        } else {
            showToast("Təhlükəsizlik üçün PIN təyin edin")
            findNavController().navigate(R.id.action_smsLogin_to_setPin)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}