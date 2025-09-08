package com.example.leoapplication.presentation.ui.fragments.otherPays

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentOtherPaysBinding

class OtherPaysFragment : Fragment() {
  private lateinit var binding: FragmentOtherPaysBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtherPaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.includeAppBar.backButton.setOnClickListener {

            findNavController().popBackStack()
        }
        binding.scrollableOtherPays.payToCard.setOnClickListener {
            findNavController().navigate(R.id.action_otherPaysFragment_to_exportToFragment)

        }
        binding.scrollableOtherPays.payToMobile.setOnClickListener {
            findNavController().navigate(R.id.action_otherPaysFragment_to_mobileBalanceFragment)
        }

        binding.scrollableOtherPays.payToWifi.setOnClickListener {
            findNavController().navigate(R.id.action_otherPaysFragment_to_wifiFragment)
        }


    }
}