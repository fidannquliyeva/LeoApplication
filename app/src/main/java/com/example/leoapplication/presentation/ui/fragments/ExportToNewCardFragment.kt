package com.example.leoapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentExportToNewCardBinding


class ExportToNewCardFragment : Fragment() {
    private lateinit var binding: FragmentExportToNewCardBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExportToNewCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar2.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_exportToNewCardFragment_to_exportToMoneyFragment)
        }

    }

}