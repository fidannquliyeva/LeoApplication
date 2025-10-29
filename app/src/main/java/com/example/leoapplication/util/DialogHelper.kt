package com.example.leoapplication.util

import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.leoapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialog


object DialogHelper {

    fun Fragment.showSupportDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_support, null)
        dialog.setContentView(view)

        view.findViewById<LinearLayout>(R.id.layoutWhatsapp).setOnClickListener {
            openLink("https://wa.me/994123101488", "com.whatsapp")
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.layoutTelegram).setOnClickListener {
            openLink("https://t.me/Leobank_bot")
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.layoutFacebook).setOnClickListener {
            openLink("https://www.facebook.com/leobank.az/")
            dialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.layoutViber).setOnClickListener {
            openLink("https://www.viber.com/leobank.az/")
            dialog.dismiss()
        }

        view.findViewById<ConstraintLayout>(R.id.layoutLanguage).setOnClickListener {
            dialog.dismiss()
            showLanguageDialog()
        }

        dialog.show()
    }

    fun Fragment.showLanguageDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_language, null)
        dialog.setContentView(view)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupLanguage)
        val radioAzerbaijan = view.findViewById<RadioButton>(R.id.radioAzerbaijan)
        val radioRussian = view.findViewById<RadioButton>(R.id.radioRussian)

        val currentLanguage = LanguageManager.getSavedLanguage(requireContext())
        if (currentLanguage == "az") {
            radioGroup.check(R.id.radioAzerbaijan)
        } else {
            radioGroup.check(R.id.radioRussian)
        }

        radioAzerbaijan.setOnClickListener {
            radioGroup.check(R.id.radioAzerbaijan)
            LanguageManager.applyLanguage(requireActivity(), "az")
            dialog.dismiss()
        }

        radioRussian.setOnClickListener {
            radioGroup.check(R.id.radioRussian)
            LanguageManager.applyLanguage(requireActivity(), "ru")
            dialog.dismiss()
        }

        dialog.show()
    }
}