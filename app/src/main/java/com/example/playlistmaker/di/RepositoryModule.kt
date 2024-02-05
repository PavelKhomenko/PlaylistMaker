package com.example.playlistmaker.di

import com.example.playlistmaker.library.favorites.data.FavoritesRepositoryImpl
import com.example.playlistmaker.library.favorites.data.converters.TrackDbConvertor
import com.example.playlistmaker.library.favorites.domain.FavoritesRepository
import com.example.playlistmaker.library.playlistDetails.data.PlaylistDetailsRepositoryImpl
import com.example.playlistmaker.library.playlistDetails.domain.api.PlaylistDetailsRepository
import com.example.playlistmaker.library.playlists.data.PlaylistRepositoryImpl
import com.example.playlistmaker.library.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get(), searchStorage = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(storage = get())
    }

    factory { TrackDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<PlaylistDetailsRepository> {
        PlaylistDetailsRepositoryImpl(get(), get())
    }
}