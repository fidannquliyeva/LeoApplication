package com.example.leoapplication.presentation.ui.fragments.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentCardBinding
import com.example.leoapplication.presentation.viewmodel.CardUiState
import com.example.leoapplication.presentation.viewmodel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardFragment : Fragment() {

    private var _binding: FragmentCardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("CardFragment", "====== CARD FRAGMENT OPENED ======")

        // ✅ Argument-dən cardId al
        val cardId = arguments?.getString("cardId")

        if (cardId != null) {
            Log.d("CardFragment", "Loading card with ID: $cardId")
            viewModel.loadCard(cardId)
        } else {
            Log.e("CardFragment", "❌ Card ID is NULL!")
            Toast.makeText(
                requireContext(),
                "⚠️ Kart ID tapılmadı",
                Toast.LENGTH_LONG
            ).show()

            // Geri qayıt
            requireActivity().onBackPressed()
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // UI State observer
                launch {
                    viewModel.uiState.collect { state ->
                        Log.d("CardFragment", "UI State: $state")
                        when (state) {
                            is CardUiState.Loading -> {
                                showLoading(true)
                                Log.d("CardFragment", "⏳ Loading card...")
                            }
                            is CardUiState.Success -> {
                                showLoading(false)
                                Log.d("CardFragment", "✅ Card loaded successfully!")
                            }
                            is CardUiState.Error -> {
                                showLoading(false)
                                val errorMessage = state.message ?: "Xəta baş verdi"
                                Log.e("CardFragment", "❌ Error: $errorMessage")
                                Toast.makeText(
                                    requireContext(),
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            is CardUiState.Message -> {
                                Log.d("CardFragment", "Message: ${state.message}")
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                // Card Data observer
                launch {
                    viewModel.card.collect { card ->
                        Log.d("CardFragment", "Card data: $card")
                        card?.let {
                            updateCardUI(it)
                            Log.d("CardFragment", "✅ Card UI updated: ${it.cardNumber}")
                        }
                    }
                }

                // Block Status observer
                launch {
                    viewModel.isBlocked.collect { isBlocked ->
                        Log.d("CardFragment", "Card blocked: $isBlocked")
                        updateBlockUI(isBlocked)
                    }
                }
            }
        }
    }

    private fun updateCardUI(card: com.example.leoapplication.data.model.Card) {
        with(binding) {
            // Kart nömrəsi (formatlanmış)
            cardCode16.text = card.cardNumber
            Log.d("CardFragment", "Card Number: ${card.cardNumber}")

            // Expiry date
            txtDate.text = card.expiryDate
            Log.d("CardFragment", "Expiry: ${card.expiryDate}")

            // CVV
            txtCvvNum.text = card.cvv
            Log.d("CardFragment", "CVV: ${card.cvv}")

            // Kartın müddəti bitibsə xəbərdarlıq
            if (card.isExpired()) {
                txtDateText.setTextColor(
                    resources.getColor(android.R.color.holo_red_dark, null)
                )
                Toast.makeText(
                    requireContext(),
                    "⚠️ Kartın müddəti bitib!",
                    Toast.LENGTH_LONG
                ).show()
                Log.w("CardFragment", "⚠️ Card is EXPIRED!")
            } else {
                txtDateText.setTextColor(
                    resources.getColor(R.color.colorUnchecked, null)
                )
            }
        }
    }

    private fun updateBlockUI(isBlocked: Boolean) {
        with(binding) {
            if (isBlocked) {
                // Kart bloklıdır
                txtBlock.text = "Plastik kartı aktivləşdir"
                txtBlockMsg.text = "Kart hal-hazırda bloklıdır"
                imgBlock.setColorFilter(
                    resources.getColor(android.R.color.holo_red_dark, null)
                )
                Log.d("CardFragment", "Card is BLOCKED")
            } else {
                // Kart aktivdir
                txtBlock.text = getString(R.string.plastik_kart_blokla)
                txtBlockMsg.text = getString(R.string.st_diyiniz_vaxt_onu_blokdan_xara_bil_rsiniz)
                imgBlock.setColorFilter(
                    resources.getColor(android.R.color.black, null)
                )
                Log.d("CardFragment", "Card is ACTIVE")
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {

            // ✅ Copy kart nömrəsi
            btnCopy.setOnClickListener {
                val cardNumber = viewModel.card.value?.cardNumber ?: ""
                if (cardNumber.isNotEmpty()) {
                    copyToClipboard(cardNumber)
                    Log.d("CardFragment", "Copied card number: $cardNumber")
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Kart nömrəsi yoxdur",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // ✅ Block/Unblock card
            imgBlock.setOnClickListener {
                showBlockConfirmation()
            }

            // ✅ Change PIN
            imgChangePin.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "PIN dəyişmək funksiyası tezliklə...",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("CardFragment", "Change PIN clicked")
                // TODO: Navigate to ChangePinFragment
            }
        }
    }

    private fun copyToClipboard(text: String) {
        try {
            val clipboard = requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Kart nömrəsi", text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                requireContext(),
                "✅ Kart nömrəsi kopyalandı",
                Toast.LENGTH_SHORT
            ).show()

            Log.d("CardFragment", "Card number copied to clipboard")
        } catch (e: Exception) {
            Log.e("CardFragment", "Copy error: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Kopyalama xətası",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showBlockConfirmation() {
        val isBlocked = viewModel.isBlocked.value
        val message = if (isBlocked) {
            "Kartı aktivləşdirmək istəyirsiniz?"
        } else {
            "Kartı bloklamaq istəyirsiniz?"
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Təsdiq")
            .setMessage(message)
            .setPositiveButton("Bəli") { _, _ ->
                Log.d("CardFragment", "Block/Unblock confirmed")
                viewModel.toggleCardBlock()
            }
            .setNegativeButton("Xeyr") { dialog, _ ->
                Log.d("CardFragment", "Block/Unblock cancelled")
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoading(show: Boolean) {
        binding.gifImageView.visibility = if (show) View.VISIBLE else View.GONE

        // Digər elementləri disable et loading zamanı
        binding.btnCopy.isEnabled = !show
        binding.imgBlock.isEnabled = !show
        binding.imgChangePin.isEnabled = !show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CardFragment", "====== CARD FRAGMENT CLOSED ======")
        _binding = null
    }
}