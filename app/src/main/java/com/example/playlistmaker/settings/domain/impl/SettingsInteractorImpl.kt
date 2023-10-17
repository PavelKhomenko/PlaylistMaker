package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(val settingsRepository: SettingsRepository) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings = settingsRepository.getThemeSettings()

    override fun updateThemeSetting(settings: ThemeSettings) =
        settingsRepository.updateThemeSetting(settings)

}