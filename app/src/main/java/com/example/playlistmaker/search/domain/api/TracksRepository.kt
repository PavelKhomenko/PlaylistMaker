package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun getTracksFromSearchHistory(): ArrayList<Track>
    fun addTrackToSearchHistory(track: Track)
    fun clearSearchHistory()
}