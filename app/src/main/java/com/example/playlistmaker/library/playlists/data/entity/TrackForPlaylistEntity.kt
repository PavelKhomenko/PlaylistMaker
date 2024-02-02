package com.example.playlistmaker.library.playlists.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table_for_playlist")
data class TrackForPlaylistEntity(
    @PrimaryKey(autoGenerate = false)
    val trackId: String,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String?,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackTime: Long,
    val previewUrl: String
)

