package com.example.leoapplication.presentation.ui.fragments.loginAuth

import android.app.DatePickerDialog
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
import androidx.navigation.fragment.navArgs
import com.example.leoapplication.data.model.User

import com.example.leoapplication.databinding.FragmentSignUpBinding
import com.example.leoapplication.presentation.viewmodel.PhoneAuthViewModel
import com.example.leoapplication.util.Constants
import com.example.leoapplication.util.Resource
import com.example.leoapplication.util.isValidEmail
import com.example.leoapplication.util.isValidFinCode
import com.example.leoapplication.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneAuthViewModel by viewModels()
    private val args: SignUpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatePicker()
        setupNextButton()
        observeViewModel()
    }

    private fun setupDatePicker() {
        binding.birthDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        "%02d/%02d/%d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    binding.birthDateEditText.setText(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }

    private fun setupNextButton() {
        binding.nextButton.setOnClickListener {
            if (validateInputs()) {
                val user = User(
                    userId = args.userId,
                    phoneNumber = args.phoneNumber,
                    fullName = binding.fullNameEditText.text.toString().trim(),
                    email = binding.emailEditText.text.toString().trim(),
                    finCode = binding.finEditText.text.toString().trim().uppercase(),
                    birthDate = binding.birthDateEditText.text.toString().trim(),
                    isVerified = true
                )

                viewModel.createUserProfile(user)
            }
        }
    }

    private fun validateInputs(): Boolean {
        val fullName = binding.fullNameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val birthDate = binding.birthDateEditText.text.toString().trim()
        val finCode = binding.finEditText.text.toString().trim()

        return when {
            fullName.isEmpty() || fullName.length < Constants.MIN_NAME_LENGTH -> {
                binding.fullNameEditText.error = "Ad və soyad ən azı 3 simvol olmalıdır"
                false
            }
            email.isEmpty() || !email.isValidEmail() -> {
                binding.emailEditText.error = "Düzgün email daxil edin"
                false
            }
            birthDate.isEmpty() -> {
                showToast("Doğum tarixi seçin")
                false
            }
            finCode.isEmpty() || !finCode.isValidFinCode() -> {
                binding.finEditText.error = "7 simvollu FIN kod daxil edin"
                false
            }
            else -> true
        }
    }

    private fun observeViewModel() {
        viewModel.userCreationState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    setLoadingState(true)
                }
                is Resource.Success -> {
                    setLoadingState(false)

                    // LoadingFragment-ə keç
                    val action = SignUpFragmentDirections.actionSignUpToLoading()
                    findNavController().navigate(action)
                }
                is Resource.Error -> {
                    setLoadingState(false)
                    showToast("Xəta: ${resource.message}", android.widget.Toast.LENGTH_LONG)
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.nextButton.isEnabled = !isLoading
        binding.nextButton.text = if (isLoading) "Yüklənir..." else "İrəli"

        binding.fullNameEditText.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.birthDateEditText.isEnabled = !isLoading
        binding.finEditText.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}