package com.example.leoapplication.util

import android.app.Activity
import android.content.Context
import java.util.Locale


object LanguageManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val DEFAULT_LANGUAGE = "az"


    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }


    private fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

 //apply language
    fun applyLanguage(activity: Activity, language: String) {
        saveLanguage(activity, language)

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = activity.resources.configuration
        config.setLocale(locale)
        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

        activity.recreate()
    }


     // app acilanda dili yujle

    fun loadLanguage(context: Context) {
        val language = getSavedLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}