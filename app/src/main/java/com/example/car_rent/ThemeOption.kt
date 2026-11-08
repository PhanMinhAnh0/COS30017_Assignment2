package com.example.car_rent

import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
object ThemeOption {

    private const val PREF_NAME = "theme_pref"
    private const val KEY_DARK_MODE = "dark_mode"
    private var darkMode = false

    //Call once at app start: loads the saved value and applies it. */
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        darkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        apply(darkMode)
    }

    fun isDarkMode(): Boolean = darkMode

    fun setDarkMode(context: Context, enabled: Boolean) {
        darkMode = enabled
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        apply(enabled)
    }

    private fun apply(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}