package com.example.playlistmaker.library.playlists.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = -1,
    val playlistName: String,
    val playlistDescription: String,
    val playlistUri: String,
    val playlistTracks: String,
    val playlistSize: Int
)