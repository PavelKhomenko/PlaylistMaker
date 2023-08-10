package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val HISTORY_KEY = "search_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun saveHistory(historyList: ArrayList<Track>) {
        val json = Gson().toJson(historyList)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    fun loadHistory(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun clearHistory(){
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

}