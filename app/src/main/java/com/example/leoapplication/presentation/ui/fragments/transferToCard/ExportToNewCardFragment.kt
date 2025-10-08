package com.example.leoapplication.presentation.ui.fragments.transferToCard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentExportToNewCardBinding


class ExportToNewCardFragment : Fragment() {
    private lateinit var binding: FragmentExportToNewCardBinding
    private val args = Bundle()


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
        val cardNumberInput = binding.cardNumberInput
        binding.nextButton.setOnClickListener{
            val cardNumber = cardNumberInput.text.toString()
            if (cardNumber.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putString("receiverCardId", cardNumber)
//                findNavController().navigate(R.id.action_exportToNewCardFragment_to_exportToMoneyFragment, bundle)
            } else {
                Toast.makeText(context, "Kart nömrəsini daxil edin", Toast.LENGTH_SHORT).show()
            }
        }




    }

}