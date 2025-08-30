package com.example.leoapplication.presentation.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

import com.example.leoapplication.databinding.FragmentProfileBinding

import com.example.leoapplication.presentation.viewmodel.ProfileVM

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileVM by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("APP_PREFS", Activity.MODE_PRIVATE)
        val phone = prefs.getString("phone", null)

        phone?.let {
            viewModel.loadUser(it)
        }


        viewModel.userData.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                binding.valueName.text = data["fullName"] as? String ?: ""
                binding.valuePhone.text = data["email"] as? String ?: ""
                binding.valuePhone.text = data["phone"] as? String ?: ""
            } else {
                Toast.makeText(requireContext(), "User tapılmadı", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
