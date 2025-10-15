package com.example.leoapplication.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}


fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


fun String.isValidFinCode(): Boolean {
    return this.length == Constants.FIN_CODE_LENGTH && this.all { it.isLetterOrDigit() }
}


fun String?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}