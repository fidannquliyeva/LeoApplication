package com.example.leoapplication.presentation.ui.fragments.increaseBalance

import androidx.fragment.app.Fragment

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncreaseOtherBankFragment : Fragment() {

//    private lateinit var binding: FragmentIncreaseOtherBankBinding
//    private val cardVM: CardVM by activityViewModels()
//    private val loginVM: LoginWithNumberVM by activityViewModels()
//
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        binding = FragmentIncreaseOtherBankBinding.inflate(inflater, container, false)
//        return binding.root
//
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        getCardInfo()
//        copyLink()
//
//
//
//    }
//
//    private fun getCardInfo(){
//        // Firebase-dən mövcud kart məlumatını çək
//        val phone = loginVM.phoneNumber
//        if (phone.isNotEmpty()) {
//            cardVM.fetchCardByPhone(phone)
//        }
//
//        // Kart məlumatı gələndə UI göstər
//        cardVM.bankCard.observe(viewLifecycleOwner) { card ->
//            card ?: return@observe
//            binding.cardNumber.text = card.cardNumber
//
//
//        }
//    }
//
//    private fun copyLink(){
//
//        binding.imgCopy.setOnClickListener {
//            // Kart nömrəsini kopyala
//            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = ClipData.newPlainText("Card Number", binding.cardNumber.text.toString())
//            clipboard.setPrimaryClip(clip)
//
//            // Toast göstər
//            Toast.makeText(requireContext(), "Kart nömrəsi kopyalandı", Toast.LENGTH_SHORT).show()
//
//            // Animasiya: qısaca böyüt və kiçilt
//            binding.imgCopy.animate()
//                .scaleX(1.3f)
//                .scaleY(1.3f)
//                .setDuration(100)
//                .withEndAction {
//                    binding.imgCopy.animate()
//                        .scaleX(1f)
//                        .scaleY(1f)
//                        .setDuration(100)
//                        .start()
//                }
//                .start()
//
//            // Rəngi dəyişdir: qısa yaşıl rəng efekti
//            binding.imgCopy.setColorFilter(resources.getColor(android.R.color.holo_green_light))
//            binding.imgCopy.postDelayed({
//                binding.imgCopy.setColorFilter(resources.getColor(R.color.colorUnchecked)) // default rənginə qayıt
//            }, 500)
//        }
//
//
//    }
}