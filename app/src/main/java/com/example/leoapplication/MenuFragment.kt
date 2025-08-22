package com.example.leoapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {

    lateinit var binding: FragmentMenuBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun goProfile() {
        binding.txtAccountMenu.setOnClickListener {
            findNavController().navigate(R.id.action_nav_menu_to_profileFragment)

        }
    }

}