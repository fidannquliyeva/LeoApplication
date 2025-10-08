package com.example.leoapplication.util

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Link açma (WhatsApp, Telegram və s.)
 */
fun Fragment.openLink(url: String, packageName: String? = null) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (packageName != null) {
            intent.setPackage(packageName)
        }
        startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "Tətbiq açılmadı", Toast.LENGTH_SHORT).show()
    }
}