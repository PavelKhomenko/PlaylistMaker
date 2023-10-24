package com.example.playlistmaker.search.data.sharedPreferences

import android.content.SharedPreferences
import com.example.playlistmaker.player.domain.model.Track
import com.google.gson.Gson

class SearchStorageImpl(private val sharedPreferences: SharedPreferences) : SearchStorage {

    private companion object {
        const val HISTORY_KEY = "search_history"
    }

    override fun getTracks(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray()
        val tracks = Gson().fromJson(json, Array<Track>::class.java)
        return tracks.reversedArray()
    }

    override fun addTrack(track: Track) {
        val tracks = read().toMutableList()
        if (tracks.contains(track)) {
            tracks.remove(track)
        }
        if (tracks.size >= 10) {
            tracks.removeAt(0)
        }
        tracks.add(track)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, Gson().toJson(tracks))
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    private fun read(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }
}