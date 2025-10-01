package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentNewUserInfoBinding
import com.example.leoapplication.databinding.FragmentSignUpBinding
import com.example.leoapplication.presentation.viewmodel.AuthViewModel
import com.example.leoapplication.presentation.viewmodel.NewUserInfoVM
import dagger.hilt.android.AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.nextButton.setOnClickListener {
            viewModel.firstNameInput.value = binding.fullNameEditText.text.toString()
            viewModel.emailInput.value = binding.emailEditText.text.toString()
            viewModel.phoneInput.value = binding.phoneNumberEditText.text.toString()

            viewModel.signUp()
        }
    }

    private fun setupObservers() {
        viewModel.signUpResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Qeydiyyat uğurlu oldu", Toast.LENGTH_SHORT).show()
                // Navigation ilə OTP fragment-ə keç
                findNavController().navigate(SignUpFragmentDirections.actionNewUserInfoFragmentToLoginWithNumberFragment())
            } else {
                Toast.makeText(requireContext(), "Qeydiyyat uğursuz oldu", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
