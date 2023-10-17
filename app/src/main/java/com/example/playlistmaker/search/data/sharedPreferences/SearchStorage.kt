package com.example.playlistmaker.search.data.sharedPreferences

import com.example.playlistmaker.player.domain.model.Track

interface SearchStorage {
    fun getTracks() : Array<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}