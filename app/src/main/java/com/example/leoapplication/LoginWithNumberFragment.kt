package com.example.leoapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentInvestmentBinding
import com.example.leoapplication.databinding.FragmentLoginWithNumberBinding


class LoginWithNumberFragment : Fragment() {

    private lateinit var binding: FragmentLoginWithNumberBinding
    private val viewModel: LoginWithNumberVM by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginWithNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goToAboutLeo()
        goToSmsLogin()
        gridLayout()

    }

    private fun goToAboutLeo(){
        binding.infoTextNext.setOnClickListener {
            findNavController().navigate(R.id.action_loginWithNumberFragment_to_aboutLeoFragment)
        }
    }
    private fun goToSmsLogin(){
        binding.nextButton.setOnClickListener {
            val currentNumber = binding.phoneNumberText.text.toString()
            if (currentNumber.length <= 13) {
                Toast.makeText(requireContext(), "Zəhmət olmasa nömrə daxil edin", Toast.LENGTH_SHORT).show()
            } else{
                findNavController().navigate(R.id.action_loginWithNumberFragment_to_smsLoginFragment)
            }
        }

    }

    private fun gridLayout() {
        val MAX_DIGITS = 9

        val numberClickListener = View.OnClickListener { view ->
            val button = view as Button
            val digit = button.text.toString()


            val currentNumber = binding.phoneNumberText.text.toString().substring(5)

            if (currentNumber.length < MAX_DIGITS) {
                binding.phoneNumberText.append(digit)
                viewModel.phoneNumber = binding.phoneNumberText.text.toString()
            }
        }

        binding.btn0.setOnClickListener(numberClickListener)
        binding.btn1.setOnClickListener(numberClickListener)
        binding.btn2.setOnClickListener(numberClickListener)
        binding.btn3.setOnClickListener(numberClickListener)
        binding.btn4.setOnClickListener(numberClickListener)
        binding.btn5.setOnClickListener(numberClickListener)
        binding.btn6.setOnClickListener(numberClickListener)
        binding.btn7.setOnClickListener(numberClickListener)
        binding.btn8.setOnClickListener(numberClickListener)
        binding.btn9.setOnClickListener(numberClickListener)


        binding.btnDelete.setOnClickListener {
            val currentText = binding.phoneNumberText.text.toString()
            if (currentText.length > 5) {
                binding.phoneNumberText.text = currentText.dropLast(1)
                viewModel.phoneNumber = binding.phoneNumberText.text.toString()
            }
        }

        binding.phoneNumberText.text = viewModel.phoneNumber.ifEmpty { "+994 " }
    }

}