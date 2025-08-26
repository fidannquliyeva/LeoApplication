package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentIncreaseOtherCardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncreaseOtherCardFragment : Fragment() {
   private lateinit var binding: FragmentIncreaseOtherCardBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncreaseOtherCardBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateToMenu()
        navigateToIncrease()

    }

    private fun navigateToMenu() {
        binding.toolbar2.setOnClickListener{
            findNavController().popBackStack()
            }
        }

    private fun navigateToIncrease() {
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_increaseOtherCardFragment_to_increaseBalanceOtherCardsFragment)
        }
    }}

