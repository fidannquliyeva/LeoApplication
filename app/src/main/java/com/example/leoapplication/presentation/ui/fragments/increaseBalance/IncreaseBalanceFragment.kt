package com.example.leoapplication.presentation.ui.fragments.increaseBalance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentIncreaseBalanceBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IncreaseBalanceFragment : Fragment() {
  private lateinit var binding: FragmentIncreaseBalanceBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIncreaseBalanceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateToHome()
        navigateToIncreaseBalance()
    }
    private fun navigateToIncreaseBalance() {
        binding.scrollableIncrease.withGoogle.setOnClickListener {
            findNavController().navigate(R.id.action_increaseBalanceFragment_to_increaseWithGoogleFragment)
        }

        binding.scrollableIncrease.otherBank.setOnClickListener {
            findNavController().navigate(R.id.action_increaseBalanceFragment_to_increaseOtherBankFragment)
        }

        binding.scrollableIncrease.otherCard.setOnClickListener {
            findNavController().navigate(R.id.action_increaseBalanceFragment_to_increaseOtherCardFragment)
        }

//        binding.scrollableIncrease.withMoney.setOnClickListener {
//            findNavController().navigate(R.id.action_increaseBalanceFragment_to_increaseWithMoneyFragment)
//        }

        binding.scrollableIncrease.withSwift.setOnClickListener {
            findNavController().navigate(R.id.action_increaseBalanceFragment_to_increaseWithSwiftFragment)
        }
    }

    private fun navigateToHome() {
        binding.includeAppBar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}