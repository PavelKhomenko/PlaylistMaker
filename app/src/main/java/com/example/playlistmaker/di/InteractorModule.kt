package com.example.playlistmaker.di

import com.example.playlistmaker.library.favorites.domain.FavoritesInteractor
import com.example.playlistmaker.library.favorites.domain.FavoritesInteractorImpl
import com.example.playlistmaker.library.playlistDetails.domain.api.PlaylistDetailsInteractor
import com.example.playlistmaker.library.playlistDetails.domain.impl.PlaylistDetailsInteractorImpl
import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<PlayerInteractor> {
        PlayerInteractorImpl(playerRepository = get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(repository = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(favoritesRepository = get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }

    single<PlaylistDetailsInteractor> {
        PlaylistDetailsInteractorImpl(playlistDetailsRepository = get())
    }
}