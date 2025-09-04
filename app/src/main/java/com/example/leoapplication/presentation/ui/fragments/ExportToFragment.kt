package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentExportToBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExportToFragment : Fragment() {

    private lateinit var binding: FragmentExportToBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExportToBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.linearLayoutNewPay.setOnClickListener {
            findNavController().navigate(R.id.action_exportToFragment_to_exportToNewCardFragment)


        }
    }


}