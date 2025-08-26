package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentIncreaseWithGoogleBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IncreaseWithGoogleFragment : Fragment() {

    private lateinit var binding: FragmentIncreaseWithGoogleBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncreaseWithGoogleBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        navigateBack()

    }

    private fun navigateBack() {
        binding.includeAppBarGoogle.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}