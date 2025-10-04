package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.leoapplication.databinding.FragmentSignUpBinding
import com.example.leoapplication.presentation.viewmodel.AuthViewModel
import com.example.leoapplication.presentation.viewmodel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val cardViewModel: CardViewModel by viewModels()

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
            authViewModel.firstNameInput.value = binding.fullNameEditText.text.toString()
            authViewModel.emailInput.value = binding.emailEditText.text.toString()
            authViewModel.phoneInput.value = binding.phoneNumberEditText.text.toString()

            authViewModel.signUp()
        }
    }

    private fun setupObservers() {
        // İstifadəçi qeydiyyatı nəticəsi
        authViewModel.signUpResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Qeydiyyat uğurlu oldu", Toast.LENGTH_SHORT).show()


                val uid = authViewModel.getCurrentUserUid()
                viewLifecycleOwner.lifecycleScope.launch {
                    cardViewModel.createCardForUser(uid)
                }

                // OTP fragment-ə dərhal keç
                findNavController().navigate(
                    SignUpFragmentDirections.actionNewUserInfoFragmentToLoginWithNumberFragment()
                )
            } else {
                Toast.makeText(requireContext(), "Qeydiyyat uğursuz oldu", Toast.LENGTH_SHORT).show()
            }
        }

        // Kart yaradılma nəticəsi
        cardViewModel.cardCreationResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                // Toast göstərmək olar, lakin UI artıq OTP ekranında ola bilər
                Toast.makeText(requireContext(), "Kart uğurla yaradıldı", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Kart yaradılmadı", Toast.LENGTH_SHORT).show()
            }
        }

        // Error-lar
        authViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        cardViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
