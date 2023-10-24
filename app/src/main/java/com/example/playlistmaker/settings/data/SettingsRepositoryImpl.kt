package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.data.dto.SettingsDto
import com.example.playlistmaker.settings.data.sharedPreferences.SettingsStorage
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val storage: SettingsStorage) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings =
        ThemeSettings(darkTheme = storage.getSettings().isDarkTheme)

    override fun updateThemeSetting(settings: ThemeSettings) =
        storage.saveSettings(SettingsDto(isDarkTheme = settings.darkTheme))

}