package com.example.playlistmaker.library.favorites.domain

import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) :
    FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) =
        favoritesRepository.addToFavorites(track)

    override suspend fun deleteFromFavorites(track: Track) =
        favoritesRepository.deleteFromFavorites(track)

    override fun getFavoritesTracks(): Flow<List<Track>> =
        favoritesRepository.getFavoritesTracks()

    override fun getFavoritesId(trackId: String): Flow<Boolean> =
        favoritesRepository.getFavoritesId(trackId)

}