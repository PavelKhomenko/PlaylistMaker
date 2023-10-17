package com.example.playlistmaker.settings.data.sharedPreferences

import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.dto.SettingsDto

class SettingsStorageImpl(private val sharedPreferences: SharedPreferences) : SettingsStorage {
    override fun getSettings(): SettingsDto = SettingsDto(
        isDarkTheme = sharedPreferences.getBoolean(
            SWITCHER_KEY, false
        )
    )

    override fun saveSettings(settingsDto: SettingsDto) {
        sharedPreferences
            .edit()
            .putBoolean(SWITCHER_KEY, settingsDto.isDarkTheme)
            .apply()
    }

    companion object {
        const val SWITCHER_KEY = "key_switcher"
    }
}