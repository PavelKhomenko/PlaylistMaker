package com.example.playlistmaker.library.db.domain

import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun deleteFromFavorites(track: Track)
    fun getFavoritesTracks(): Flow<List<Track>>
    fun getFavoritesId(trackId: String): Flow<Boolean>
}