package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leoapplication.R
import com.example.leoapplication.presentation.ui.adapters.RecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoFragment : Fragment(R.layout.fragment_crypto) {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_crypto, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Dummy data
            val list = mutableListOf<String>()
            for (i in 1..30) {
                list.add("Item #$i")
            }

            recyclerView.adapter = RecyclerAdapter(list)
        }
    }

