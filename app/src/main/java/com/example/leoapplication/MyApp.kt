package com.example.leoapplication

import android.app.Application
import com.example.leoapplication.util.LanguageManager
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        LanguageManager.loadLanguage(this)
    }
}