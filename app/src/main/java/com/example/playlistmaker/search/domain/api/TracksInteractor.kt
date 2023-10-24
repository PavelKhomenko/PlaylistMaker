package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.player.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun getTracksFromSearchHistory(): ArrayList<Track>
    fun addTrackToSearchHistory(track: Track)
    fun clearSearchHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, message: String?)
    }
}