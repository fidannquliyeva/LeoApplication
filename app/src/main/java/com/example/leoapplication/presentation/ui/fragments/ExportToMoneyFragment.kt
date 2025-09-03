package com.example.leoapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentExportToMoneyBinding

class ExportToMoneyFragment : Fragment() {

    private lateinit var binding: FragmentExportToMoneyBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExportToMoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar2.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnPay.setOnClickListener {
         findNavController().navigate(R.id.action_exportToMoneyFragment_to_paymentSuccessFragment)
        }


    }
}