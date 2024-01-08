package com.example.playlistmaker.di

import com.example.playlistmaker.application.App
import com.example.playlistmaker.library.presentation.FavoritesViewModel
import com.example.playlistmaker.library.presentation.PlaylistViewModel
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { PlayerViewModel(playerInteractor = get(), favoritesInteractor = get()) }
    viewModel { SearchViewModel(tracksInteractor = get()) }
    viewModel {
        SettingsViewModel(
            application = androidApplication() as App,
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }
    viewModel { FavoritesViewModel(favoritesInteractor = get()) }
    viewModel { PlaylistViewModel() }
}