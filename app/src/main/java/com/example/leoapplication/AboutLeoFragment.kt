package com.example.leoapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.leoapplication.databinding.FragmentAboutLeoBinding


class AboutLeoFragment : Fragment() {
   private lateinit var binding: FragmentAboutLeoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutLeoBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goBack()
        leoTerms()
    }

    private fun goBack(){
        binding.btnGoBack.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun leoTerms(){

       binding.txtRule.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile-static.leobank.az/pages/oferta.html"))
            startActivity(intent)
        }
        binding.txtCredit.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile-static.leobank.az/pages/akb.html"))
            startActivity(intent)
        }
        binding.txtASAN.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile-static.leobank.az/pages/asan.html"))
            startActivity(intent)
        }
        binding.txtTarif.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile-static.leobank.az/pages/tarif.html"))
            startActivity(intent)
        }
        binding.txtSecurity.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://leobank.az/az/terms"))
            startActivity(intent)
        }

    }
}

