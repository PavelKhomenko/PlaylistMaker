package com.example.playlistmaker.settings.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.application.App
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val application: App
) : AndroidViewModel(application) {

    private var darkTheme = false
    private val themeSwitherStateLiveData = MutableLiveData(darkTheme)

    init {
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
        themeSwitherStateLiveData.value = darkTheme
    }

    fun observeThemeSwitcherState(): LiveData<Boolean> = themeSwitherStateLiveData

    fun onShareAppClicked() = sharingInteractor.shareApp()
    fun onOpenSupportClicked() = sharingInteractor.openSupport()
    fun onOpenTermsClicked() = sharingInteractor.openTerms()

    fun onThemeSwitcherClicked(isChecked: Boolean) {
        themeSwitherStateLiveData.value = isChecked
        settingsInteractor.updateThemeSetting(ThemeSettings(darkTheme = isChecked))
        switchTheme(isChecked)
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}