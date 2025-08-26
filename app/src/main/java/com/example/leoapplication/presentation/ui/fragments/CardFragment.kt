package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardVisa = view.findViewById<View>(R.id.card_visa_card)
        val cardLine = view.findViewById<View>(R.id.cardLine)
        val btnCopy = view.findViewById<View>(R.id.btnCopy)
        val txtDate = view.findViewById<View>(R.id.txtDate)
        val txtDateText = view.findViewById<View>(R.id.txtDateText)
        val txtCvvNum = view.findViewById<View>(R.id.txtCvvNum)
        val txtCvv = view.findViewById<View>(R.id.txtCvv)

        cardVisa.transitionName = "card_transition"

        // Kart flip animasiyası açılarkən
        cardVisa.rotationY = 90f
        cardVisa.animate()
            .rotationY(0f)
            .setDuration(200)
            .start()

        // Kart kliklənəndə geri dön və flip animasiyası
        cardVisa.setOnClickListener {

            cardLine.isInvisible = true
            txtDate.isInvisible = true
            txtDateText.isInvisible = true
            txtCvvNum.isInvisible = true
            txtCvv.isInvisible = true
            btnCopy.isInvisible = true

            cardVisa.animate()
                .rotationY(90f)
                .setDuration(200)
                .withEndAction {
                    findNavController().popBackStack()
                }
                .start()
        }
    }
}
