package com.example.playlistmaker.search.data.sharedPreferences

import android.content.SharedPreferences
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.utils.Const.HISTORY_KEY
import com.google.gson.Gson

class SearchStorageImpl(private val sharedPreferences: SharedPreferences, private val gson: Gson) : SearchStorage {

    override fun getTracks(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray()
        val tracks = gson.fromJson(json, Array<Track>::class.java)
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
            .putString(HISTORY_KEY, gson.toJson(tracks))
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    private fun read(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray()
        return gson.fromJson(json, Array<Track>::class.java)
    }
}