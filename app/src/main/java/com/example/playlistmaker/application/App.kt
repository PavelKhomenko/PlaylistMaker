package com.example.playlistmaker.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val THEME_PREFERENCES = "dark_theme"
const val SWITCHER_KEY = "key_switcher"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        darkTheme = getSavedTheme()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        saveTheme(darkTheme)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun saveTheme(darkThemeEnabled: Boolean) {
        val sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(SWITCHER_KEY, darkThemeEnabled)
        editor.apply()
    }

    private fun getSavedTheme(): Boolean {
        val sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        return sharedPreferences.getBoolean(SWITCHER_KEY, false)
    }
}