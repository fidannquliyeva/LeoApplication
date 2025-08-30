package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    ): View {
        binding = FragmentIncreaseOtherCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar arxaya qayıtmaq
        binding.toolbar2.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextButton.setOnClickListener {
            val cardNumber = binding.cardNumberInput.text.toString()
            val cvv = binding.cvvInput.text.toString()
            val expiry = binding.expiryInput.text.toString()

            if (cardNumber.isEmpty() || cvv.isEmpty() || expiry.isEmpty()) {
                Toast.makeText(context, "Bütün sahələri doldurun!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mm = expiry.split("/")[0]
            val yy = expiry.split("/")[1]

            val bundle = Bundle().apply {
                putString("cardNumber", cardNumber)
                putString("cvv", cvv)
                putString("mm", mm)
                putString("yy", yy)
            }

            findNavController().navigate(
                R.id.action_increaseOtherCardFragment_to_increaseBalanceOtherCardsFragment,
                bundle
            )
        }
    }
}
