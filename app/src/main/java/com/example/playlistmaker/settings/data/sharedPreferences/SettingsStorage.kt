package com.example.playlistmaker.settings.data.sharedPreferences

import com.example.playlistmaker.settings.data.dto.SettingsDto

interface SettingsStorage {
    fun getSettings(): SettingsDto
    fun saveSettings(settingsDto: SettingsDto)
}