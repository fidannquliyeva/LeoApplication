package com.example.leoapplication.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * Avatar idarəetməsi - Davamlı saxlama ilə
 */
object AvatarManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_AVATAR_PATH = "avatar_path"
    private const val AVATAR_FILE_NAME = "user_avatar.jpg"

    /**
     * Avatar-ı internal storage-a kopyalayıb saxla
     */
    fun saveAvatar(context: Context, uri: Uri) {
        try {
            // Şəkli oxu
            val inputStream = context.contentResolver.openInputStream(uri)

            if (inputStream == null) {
                Log.e("AvatarManager", "❌ Cannot open input stream")
                return
            }

            // Internal storage-da fayl yarat
            val avatarFile = File(context.filesDir, AVATAR_FILE_NAME)

            // Şəkli kopyala
            val outputStream = FileOutputStream(avatarFile)
            inputStream.copyTo(outputStream)

            // Stream-ləri bağla
            inputStream.close()
            outputStream.close()

            // Fayl path-ini SharedPreferences-də saxla
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_AVATAR_PATH, avatarFile.absolutePath)
                .apply()

            Log.d("AvatarManager", "✅ Avatar saved: ${avatarFile.absolutePath}")

        } catch (e: Exception) {
            Log.e("AvatarManager", "❌ Avatar save failed: ${e.message}", e)
        }
    }

    /**
     * ✅ Avatar-ın file path-ini al
     */
    fun getAvatar(context: Context): String? {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val path = prefs.getString(KEY_AVATAR_PATH, null)

            if (path != null) {
                val file = File(path)

                // Fayl mövcuddurmu?
                if (file.exists()) {
                    Log.d("AvatarManager", "✅ Avatar found: $path")
                    return path
                } else {
                    Log.w("AvatarManager", "⚠️ Avatar file not found, clearing...")
                    clearAvatar(context)
                }
            }

        } catch (e: Exception) {
            Log.e("AvatarManager", "❌ Get avatar failed: ${e.message}", e)
        }

        return null
    }

    /**
     * ✅ Avatar-ı sil
     */
    fun clearAvatar(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val path = prefs.getString(KEY_AVATAR_PATH, null)

            // Faylı sil
            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    Log.d("AvatarManager", "✅ Avatar file deleted")
                }
            }

            // SharedPreferences-dən sil
            prefs.edit().remove(KEY_AVATAR_PATH).apply()

            Log.d("AvatarManager", "✅ Avatar cleared")

        } catch (e: Exception) {
            Log.e("AvatarManager", "❌ Clear avatar failed: ${e.message}", e)
        }
    }

    /**
     * ✅ Avatar var?
     */
    fun hasAvatar(context: Context): Boolean {
        return getAvatar(context) != null
    }
}