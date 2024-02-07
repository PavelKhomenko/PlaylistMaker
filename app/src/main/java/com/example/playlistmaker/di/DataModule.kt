package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.library.favorites.data.db.AppDatabase
import com.example.playlistmaker.library.playlists.data.converters.PlaylistDbConverter
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.sharedPreferences.SearchStorage
import com.example.playlistmaker.search.data.sharedPreferences.SearchStorageImpl
import com.example.playlistmaker.settings.data.sharedPreferences.SettingsStorage
import com.example.playlistmaker.settings.data.sharedPreferences.SettingsStorageImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.utils.Const.HISTORY_KEY
import com.example.playlistmaker.utils.Const.ITUNES_BASE_URL
import com.example.playlistmaker.utils.Const.THEME_PREFERENCES
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            //.fallbackToDestructiveMigration()
            .build()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(context = androidContext(), itunesService = get())
    }

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single(named(HISTORY_KEY)) {
        androidContext()
            .getSharedPreferences(HISTORY_KEY, Context.MODE_PRIVATE)
    }

    single<SearchStorage> {
        SearchStorageImpl(sharedPreferences = get(named(HISTORY_KEY)), gson = get())
    }

    factory { Gson() }


    single(named(THEME_PREFERENCES)) {
        androidContext()
            .getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<SettingsStorage> { SettingsStorageImpl(sharedPreferences = get(named(THEME_PREFERENCES))) }

    single<ExternalNavigator> { ExternalNavigatorImpl(context = androidContext()) }

    factory { PlaylistDbConverter(get()) }

}