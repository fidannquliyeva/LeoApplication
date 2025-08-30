package com.example.leoapplication.presentation.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

import com.example.leoapplication.databinding.FragmentProfileBinding

import com.example.leoapplication.presentation.viewmodel.ProfileVM
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Log.d("ProfileFragment", "Current UID: $uid")
        viewModel.loadUserProfile(uid)

        observeProfile()
    }

    private fun observeProfile() {
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.valueName.text = it.fullName
                binding.valueEmail.text = it.email
                binding.valuePhone.text = it.phone
                // avatar varsa Glide ilə yükləyə bilərsiniz
                // Glide.with(this).load(it.avatarUrl).into(binding.imgAvatar)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }
}
