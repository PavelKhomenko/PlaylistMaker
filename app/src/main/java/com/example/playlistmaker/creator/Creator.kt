package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.application.App
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.sharedPreferences.SearchStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.sharedPreferences.SettingsStorage
import com.example.playlistmaker.settings.data.sharedPreferences.SettingsStorageImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    fun providePlayerInteractor(): PlayerInteractor = PlayerInteractorImpl(PlayerRepositoryImpl())

    fun provideTracksInteractor(context: Context): TracksInteractor =
        TracksInteractorImpl(getTracksRepository(context))

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(context),
            SearchStorageImpl(
                context.getSharedPreferences(
                    "track_list_shared_preferences",
                    Context.MODE_PRIVATE
                )
            )
        )
    }

    fun provideSharingInteractor(context: Context): SharingInteractor =
        SharingInteractorImpl(externalNavigator = getExternalNavigator(context))

    fun provideSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(settingsRepository = getSettingsRepository(context))

    private fun getExternalNavigator(context: Context): ExternalNavigator =
        ExternalNavigatorImpl(context = context)

    private fun getSettingsRepository(context: Context): SettingsRepository =
        SettingsRepositoryImpl(storage = getSettingsStorage(context))

    private fun getSettingsStorage(context: Context): SettingsStorage =
        SettingsStorageImpl(sharedPreferences = getSettingsPreferences(context))

    private fun getSettingsPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(
            App.THEME_PREFERENCES, AppCompatActivity.MODE_PRIVATE
        )

}