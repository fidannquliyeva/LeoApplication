package com.example.leoapplication.presentation.ui.fragments.increaseBalance

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.leoapplication.databinding.FragmentIncreaseBalanceBinding
import com.example.leoapplication.databinding.FragmentIncreaseOtherBankBinding
import com.example.leoapplication.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IncreaseOtherBankFragment : Fragment() {

    private var _binding: FragmentIncreaseOtherBankBinding? = null
    private val binding get() = _binding!!


    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncreaseOtherBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observeSelectedCard()
        setupCopyButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun observeSelectedCard() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.selectedCard.collect { card ->
                    card?.let {
                        binding.cardNumber.text = it.cardNumber
                    } ?: run {
                        binding.cardNumber.text = "Kart yoxdur"
                    }
                }
            }
        }
    }

    private fun setupCopyButton() {
        binding.imgCopy.setOnClickListener {
            val cardNumber = binding.cardNumber.text.toString()

            if (cardNumber.isNotEmpty() && cardNumber != "Kart yoxdur") {

                copyToClipboard(cardNumber)
                showCopySuccess()

                Toast.makeText(
                    requireContext(),
                    "Kart nömrəsi kopyalandı",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Kart nömrəsi yoxdur",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun showCopySuccess() {
        _binding?.let { binding ->
            binding.imgCopy.setColorFilter(requireContext().getColor(android.R.color.holo_green_dark))

            binding.imgCopy.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(150)
                .withEndAction {
                    binding.imgCopy.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start()
                }
                .start()

            binding.imgCopy.postDelayed({
                if (isAdded && view != null && _binding != null) {
                    resetCopyButton()
                }
            }, 2000)
        }
    }

    private fun resetCopyButton() {
        _binding?.imgCopy?.clearColorFilter()
    }


    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Kart nömrəsi", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}