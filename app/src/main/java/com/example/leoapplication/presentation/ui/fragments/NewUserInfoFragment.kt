package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentNewUserInfoBinding
import com.example.leoapplication.presentation.viewmodel.NewUserInfoVM
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class NewUserInfoFragment : Fragment() {

    private lateinit var binding: FragmentNewUserInfoBinding
    private val viewModel: NewUserInfoVM by activityViewModels()
    private var phoneNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            phoneNumber = NewUserInfoFragmentArgs.fromBundle(args).phone
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // User və kart yaradıldıqdan sonra yönləndirmə
        viewModel.registrationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                val action = NewUserInfoFragmentDirections
                    .actionNewUserInfoFragmentToSmsLoginFragment(phoneNumber)
                findNavController().navigate(action)
            }
        }

        // Kart yaradıldıqda məlumat göstərə bilərsən
        viewModel.bankCard.observe(viewLifecycleOwner) { card ->
            card?.let {
                Toast.makeText(requireContext(), "Kart yaradıldı: ${it.cardNumber}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupListeners() {
        binding.nextButton.setOnClickListener {
            val fullName = binding.fullNameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()

            if (fullName.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "Bütün sahələri doldurun", Toast.LENGTH_SHORT).show()
            } else {
                // User və kart yaradılır
                viewModel.registerUser(fullName, email, phoneNumber)
            }
        }
    }
}
