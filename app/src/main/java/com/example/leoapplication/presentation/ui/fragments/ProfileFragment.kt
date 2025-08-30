package com.example.leoapplication.presentation.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentProfileBinding
import com.example.leoapplication.presentation.viewmodel.ProfileVM
import com.example.leoapplication.presentation.viewmodel.UserProfileVM
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

    private const val AVATAR_REQUEST_CODE = 1001


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModelU: UserProfileVM by viewModels()
    private val viewModelP: ProfileVM by viewModels()


    private val userPhone = "+994501234567" // Burada real login istifadəçi nömrəsini istifadə et

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelU.loadUser(userPhone)

        viewModelU.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.valueName.text = it.fullName
                binding.valuePhone.text = it.phone
                binding.valueEmail.text = it.email
//                if (it.avatar.isNotEmpty()) {
//                    Glide.with(this).load(it.avatar).into(binding.imgAvatar)
//                }
//            }
            }
//
//            binding.imgAvatar.setOnClickListener {
//                // Gallery açmaq üçün intent
//                val intent = Intent(Intent.ACTION_PICK)
//                intent.type = "image/*"
//                startActivityForResult(intent, AVATAR_REQUEST_CODE)
//            }


        }

//    // onActivityResult ilə şəkli yüklə
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == AVATAR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val selectedImage = data?.data
//            selectedImage?.let { uri ->
//                val phone = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: return
//                viewModelP.updateAvatar(phone, uri)
//            }
//        }
//    }
    }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
