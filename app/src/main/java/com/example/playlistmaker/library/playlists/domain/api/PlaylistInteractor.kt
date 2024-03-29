package com.example.playlistmaker.library.playlists.domain.api

import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackInPlaylist(track: Track)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun deleteCurrentTrackFromPlaylist(trackId: String, playlistId: Int): Flow<List<Track>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun editPlaylist(playlist: Playlist)
}