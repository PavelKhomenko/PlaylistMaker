package com.example.playlistmaker.library.playlistDetails.domain.impl

import com.example.playlistmaker.library.playlistDetails.domain.api.PlaylistDetailsInteractor
import com.example.playlistmaker.library.playlistDetails.domain.api.PlaylistDetailsRepository
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistDetailsInteractorImpl(private val playlistDetailsRepository: PlaylistDetailsRepository) : PlaylistDetailsInteractor {

    override fun getDetailedPlaylist(id: Int): Flow<Playlist> {
        return playlistDetailsRepository.getDetailedPlaylist(id)
    }

    override fun getTracksFromPlaylist(tracks: List<String>): Flow<List<Track>> {
        return playlistDetailsRepository.getTracksFromPlaylist(tracks)
    }
}