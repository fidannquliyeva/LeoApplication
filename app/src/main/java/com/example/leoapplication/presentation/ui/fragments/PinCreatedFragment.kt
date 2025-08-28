package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentPinCreatedBinding

import com.example.leoapplication.presentation.viewmodel.PinCreatedVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinCreatedFragment : Fragment() {

    private lateinit var binding: FragmentPinCreatedBinding
    private val viewModel: PinCreatedVM by viewModels()
    private val enteredPin = StringBuilder()
    private val confirmPin = StringBuilder()
    private var isConfirmPhase = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPinCreatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPad()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.pinSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(requireContext(), "PIN yadda saxlanıldı", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_pinCreatedFragment_to_pinLoginFragment)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupNumberPad() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        buttons.forEach { btn ->
            btn.setOnClickListener {
                if (!isConfirmPhase) {
                    if (enteredPin.length < 4) enteredPin.append(btn.text)
                    updatePinIndicators(binding.pin1, binding.pin2, binding.pin3, binding.pin4, enteredPin.length)
                    if (enteredPin.length == 4) startConfirmPhase()
                } else {
                    if (confirmPin.length < 4) confirmPin.append(btn.text)
                    updatePinIndicators(binding.pinC1, binding.pinC2, binding.pinC3, binding.pinC4, confirmPin.length)
                    if (confirmPin.length == 4) finishPinCreation()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            if (!isConfirmPhase) {
                if (enteredPin.isNotEmpty()) enteredPin.deleteCharAt(enteredPin.lastIndex)
                updatePinIndicators(binding.pin1, binding.pin2, binding.pin3, binding.pin4, enteredPin.length)
            } else {
                if (confirmPin.isNotEmpty()) confirmPin.deleteCharAt(confirmPin.lastIndex)
                updatePinIndicators(binding.pinC1, binding.pinC2, binding.pinC3, binding.pinC4, confirmPin.length)
            }
        }
    }

    private fun startConfirmPhase() {
        isConfirmPhase = true
        viewModel.setPin(enteredPin.toString())
    }

    private fun finishPinCreation() {
        viewModel.setConfirmPin(confirmPin.toString())
        viewModel.savePin()
    }

    private fun updatePinIndicators(p1: ImageView, p2: ImageView, p3: ImageView, p4: ImageView, filled: Int) {
        val pins = listOf(p1, p2, p3, p4)
        pins.forEachIndexed { index, imageView ->
            imageView.setColorFilter(
                if (index < filled) resources.getColor(R.color.black)
                else resources.getColor(R.color.colorUnchecked)
            )
        }
    }
}
