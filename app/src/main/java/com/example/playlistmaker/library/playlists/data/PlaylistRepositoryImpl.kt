package com.example.playlistmaker.library.playlists.data

import com.example.playlistmaker.library.favorites.data.db.AppDatabase
import com.example.playlistmaker.library.playlists.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.library.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDataBase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDataBase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun addTrackInPlaylist(track: Track) {
        appDataBase.trackForPlaylistDao()
            .insertTrackForPlaylists(playlistDbConverter.map(track))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDataBase.playlistDao()
            .updatePlaylist(
                tracks = playlistDbConverter.map(playlist.playlistTracks),
                amount = playlist.playlistSize,
                idPlaylist = playlist.id
            )
    }

    override fun getPlaylists(): Flow<List<Playlist>> =
        appDataBase.playlistDao().getPlaylists().map {
            convertFromEntity(it)
        }

    private fun convertFromEntity(list: List<PlaylistEntity>): List<Playlist> =
        list.map { playlist -> playlistDbConverter.map(playlist) }

}