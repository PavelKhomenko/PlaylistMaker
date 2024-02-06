package com.example.playlistmaker.library.playlists.domain.impl

import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) =
        playlistRepository.createPlaylist(playlist)


    override suspend fun addTrackInPlaylist(track: Track) =
        playlistRepository.addTrackInPlaylist(track)


    override suspend fun updatePlaylist(playlist: Playlist) =
        playlistRepository.updatePlaylist(playlist)


    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistRepository.getPlaylists()

    override suspend fun deleteCurrentTrackFromPlaylist(
        trackId: String,
        playlistId: Int
    ): Flow<List<Track>> {
        return playlistRepository.deleteCurrentTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        return playlistRepository.deletePlaylist(playlist)
    }

}