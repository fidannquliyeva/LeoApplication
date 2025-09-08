package com.example.leoapplication.presentation.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentPaymentSuccessBinding


class PaymentSuccessFragment : Fragment() {
    private lateinit var binding: FragmentPaymentSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val amount = arguments?.getDouble("amount")
        val receiver = arguments?.getString("receiverCardId")

        val tvAmount = binding.tvAmount
        val tvReceiver =binding.tvReceiver
        tvAmount.text = "$amount ₼"
        tvReceiver.text = receiver
        binding.btnReady.setOnClickListener {
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_nav_home)
        }
    }


}