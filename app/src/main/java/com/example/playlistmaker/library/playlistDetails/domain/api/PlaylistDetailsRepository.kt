package com.example.playlistmaker.library.playlistDetails.domain.api

import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistDetailsRepository {

    fun getDetailedPlaylist(id: Int): Flow<Playlist>
    fun getTracksFromPlaylist(tracks: List<String>): Flow<List<Track>>
}