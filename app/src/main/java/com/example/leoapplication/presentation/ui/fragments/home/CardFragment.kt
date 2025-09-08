package com.example.leoapplication.presentation.ui.fragments.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentCardBinding
import com.example.leoapplication.presentation.viewmodel.CardVM
import com.example.leoapplication.presentation.viewmodel.LoginWithNumberVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private val binding get() = _binding!!
    private var isAnimationPlayed = false // flag əlavə et

    private val cardVM: CardVM by activityViewModels()
    private val loginVM: LoginWithNumberVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Başlanğıcda bütün kart elementlərini gizlət
        listOf(
            binding.cardVisaCard,
            binding.cardLine,
            binding.btnCopy,
            binding.txtDate,
            binding.txtDateText,
            binding.txtCvvNum,
            binding.txtCvv,
            binding.cardCode16
        ).forEach { it.visibility = View.INVISIBLE }

        // Firebase-dən mövcud kart məlumatını çək
        val phone = loginVM.phoneNumber
        if (phone.isNotEmpty()) {
            cardVM.fetchCardByPhone(phone)
        }

        // Kart məlumatı gələndə UI göstər
        cardVM.bankCard.observe(viewLifecycleOwner) { card ->
            card ?: return@observe

            listOf(
                binding.cardVisaCard,
                binding.cardLine,
                binding.btnCopy,
                binding.txtDate,
                binding.txtDateText,
                binding.txtCvvNum,
                binding.txtCvv,
                binding.cardCode16
            ).forEach { it.visibility = View.VISIBLE }

            binding.cardCode16.text = card.cardNumber
            binding.txtDate.text = "${card.mm}/${card.yy}"
            binding.txtCvvNum.text = card.cvv

            // Animasiya yalnız ilk dəfə işə düşsün
            if (!isAnimationPlayed) {
                isAnimationPlayed = true
                binding.cardVisaCard.rotationY = 90f
                binding.cardVisaCard.animate().rotationY(0f).setDuration(200).start()
            }
        }
        cardVM.error.observe(viewLifecycleOwner) {
            it?.let { msg ->
                android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Kart kliklənəndə geri dön
        binding.cardVisaCard.setOnClickListener {
            listOf(
                binding.cardLine,
                binding.txtDate,
                binding.txtDateText,
                binding.txtCvvNum,
                binding.txtCvv,
                binding.btnCopy
            ).forEach { it.isInvisible = true }

            binding.cardVisaCard.animate()
                .rotationY(90f)
                .setDuration(200)
                .withEndAction { findNavController().popBackStack() }
                .start()
        }

        // Copy funksionallığı

        binding.btnCopy.setOnClickListener {
            // Kart nömrəsini kopyala
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Card Number", binding.cardCode16.text.toString())
            clipboard.setPrimaryClip(clip)

            // Toast göstər
            Toast.makeText(requireContext(), "Kart nömrəsi kopyalandı", Toast.LENGTH_SHORT).show()

            // Animasiya: qısaca böyüt və kiçilt
            binding.btnCopy.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(100)
                .withEndAction {
                    binding.btnCopy.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

            // Rəngi dəyişdir: qısa yaşıl rəng efekti
            binding.btnCopy.setColorFilter(resources.getColor(android.R.color.holo_green_light))
            binding.btnCopy.postDelayed({
                binding.btnCopy.setColorFilter(resources.getColor(R.color.colorUnchecked)) // default rənginə qayıt
            }, 500)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
