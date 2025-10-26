package com.example.leoapplication.presentation.ui.fragments.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
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

    private var isCvvVisible = false
    private var actualCvv: String = ""

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

        // ⭐ HARDWARE BACK düyməsini deaktiv edirik
        disableBackButton()

        val cardId = arguments?.getString("cardId")

        if (cardId != null) {
            Log.d("CardFragment", "Loading card with ID: $cardId")
            viewModel.loadCard(cardId)
        } else {
            Log.e("CardFragment", " Card ID is NULL!")
            Toast.makeText(
                requireContext(),
                "Kart ID tapılmadı",
                Toast.LENGTH_LONG
            ).show()

            requireActivity().onBackPressed()
        }

        setupObservers()
        setupClickListeners()
    }

    /**
     * Hardware back düyməsini deaktiv edir
     * Yalnız karta toxunanda geri qayıtmaq üçün
     */
    private fun disableBackButton() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(
                    requireContext(),
                    "Geri qayıtmaq üçün karta toxunun",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("CardFragment", "🚫 Back button pressed - BLOCKED")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

        private fun flipAndGoBack() {
            Log.d("CardFragment", "🔄 Card clicked - Flipping entire layout")

            val cardContainer = binding.root // Əgər sənin XML-də "root" və ya "mainContainer" kimi adlanırsa, onu yaz

            cardContainer.animate()
                .rotationY(180f)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .alpha(0.0f)
                .setDuration(700)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    Log.d("CardFragment", "✅ Flip animation finished - Going back to Home")
                    findNavController().popBackStack()
                }
                .start()
        }


    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

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
                                Log.d("CardFragment", "💬 Message: ${state.message}")
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.card.collect { card ->
                        Log.d("CardFragment", "Card data: $card")
                        card?.let {
                            updateCardUI(it)
                            Log.d("CardFragment", "Card UI updated: ${it.cardNumber}")
                        }
                    }
                }

                launch {
                    viewModel.isBlocked.collect { isBlocked ->
                        updateBlockUI(isBlocked)
                    }
                }
            }
        }
    }

    private fun updateCardUI(card: com.example.leoapplication.data.model.Card) {
        with(binding) {

            cardCode16.text = card.cardNumber
            txtDate.text = card.expiryDate

            actualCvv = card.cvv
            txtCvvNum.text = actualCvv

            showCvvSticker()

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
                txtBlock.text = "Plastik kartı aktivləşdir"
                txtBlockMsg.text = "Kart hal-hazırda bloklıdır"
                imgBlock.setColorFilter(
                    resources.getColor(android.R.color.holo_red_dark, null)
                )
                Log.d("CardFragment", "🔒 Card is BLOCKED")
            } else {
                txtBlock.text = getString(R.string.plastik_kart_blokla)
                txtBlockMsg.text = getString(R.string.st_diyiniz_vaxt_onu_blokdan_xara_bil_rsiniz)
                imgBlock.setColorFilter(
                    resources.getColor(android.R.color.black, null)
                )
                Log.d("CardFragment", "✅ Card is ACTIVE")
            }
        }
    }

    private fun showCvvStickerWithAnimation() {
        binding.cvvStickerOverlay.apply {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            translationX = 200f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationX(0f)
                .rotation(0f)
                .setDuration(400)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .withStartAction {
                    isCvvVisible = false
                }
                .withEndAction {
                    Log.d("CardFragment", "🔒 CVV sticker shown with animation")
                }
                .start()
        }
    }

    private fun showCvvSticker() {
        binding.cvvStickerOverlay.apply {
            clearAnimation()
            visibility = View.VISIBLE
            alpha = 1f
            scaleX = 1f
            scaleY = 1f
            rotation = 0f
            translationX = 0f
            translationY = 0f
        }
        isCvvVisible = false
        Log.d("CardFragment", "🔒 CVV sticker shown - CVV hidden")
    }

    private fun hideCvvStickerWithAnimation() {
        binding.cvvStickerOverlay.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .rotation(98f)
            .translationX(200f)
            .setDuration(300)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                binding.cvvStickerOverlay.visibility = View.GONE
                isCvvVisible = true
                Log.d("CardFragment", "🔓 CVV sticker removed - CVV visible")

                Toast.makeText(
                    requireContext(),
                    "CVV görünür",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .start()
    }

    private fun setupClickListeners() {
        with(binding) {

            btnCopy.setOnClickListener {
                val cardNumber = viewModel.card.value?.cardNumber ?: ""
                if (cardNumber.isNotEmpty()) {
                    copyToClipboard(cardNumber)
                    Log.d("CardFragment", "📋 Copied card number: $cardNumber")
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Kart nömrəsi yoxdur",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            imgBlock.setOnClickListener {
                showBlockConfirmation()
            }

            cvvStickerOverlay.setOnClickListener {
                if (!isCvvVisible) {
                    hideCvvStickerWithAnimation()
                }
            }

            txtCvvNum.setOnClickListener {
                if (isCvvVisible) {
                    showCvvStickerWithAnimation()
                    Toast.makeText(
                        requireContext(),
                        "CVV gizlədildi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // ⭐ KARTA TOXUNANDA - Y OXUNDA FLİP (ÖN/ARXA), KİÇİLİR VƏ GERİ QAYIDIR
            cardVisaCard.setOnClickListener {
                Log.d("CardFragment", "🎴 Card clicked - Starting flip animation")
                flipAndGoBack()
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
                "Kart nömrəsi kopyalandı",
                Toast.LENGTH_SHORT
            ).show()

            Log.d("CardFragment", "📋 Card number copied to clipboard")
        } catch (e: Exception) {
            Log.e("CardFragment", "❌ Copy error: ${e.message}")
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
                Log.d("CardFragment", "✅ Block/Unblock confirmed")
                viewModel.toggleCardBlock()
            }
            .setNegativeButton("Xeyr") { dialog, _ ->
                Log.d("CardFragment", "❌ Block/Unblock cancelled")
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoading(show: Boolean) {
        binding.btnCopy.isEnabled = !show
        binding.imgBlock.isEnabled = !show
        binding.imgChangePin.isEnabled = !show
        binding.cvvStickerOverlay.isEnabled = !show
        binding.cardVisaCard.isEnabled = !show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}