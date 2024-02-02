package com.example.playlistmaker.library.favorites.data

import com.example.playlistmaker.library.favorites.data.converters.TrackDbConvertor
import com.example.playlistmaker.library.favorites.data.db.AppDatabase
import com.example.playlistmaker.library.favorites.data.entity.TrackEntity
import com.example.playlistmaker.library.favorites.domain.FavoritesRepository
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {
    override suspend fun addToFavorites(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    override fun getFavoritesTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTrack().map {
            convertFromEntity(it)
        }
    }

    override fun getFavoritesId(trackId: String): Flow<Boolean> = flow {
        emit(appDatabase.trackDao().getTrackId().contains(trackId))
    }

    private fun convertFromEntity(tracks: List<TrackEntity>): List<Track> =
        tracks.map { trackEntity -> trackDbConvertor.map(trackEntity) }

}
