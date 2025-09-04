package com.example.leoapplication.presentation.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.leoapplication.R
import com.example.leoapplication.databinding.FragmentTransactionDetailBinding
import com.example.leoapplication.domain.model.Transaction

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar2.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val transaction = arguments?.getSerializable("transaction") as? Transaction

        transaction?.let {
            binding.imgIcon.setImageResource(it.iconRes)
            binding.tvTitle.text = it.title
            binding.tvSubtitle.text = it.subtitle
            binding.tvAmount.text = String.format("%.2f", it.amount)

//            binding.tvAmount.setTextColor(
//                if (it.isIncome) Color.parseColor("#4CAF50") else Color.BLACK
//            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
