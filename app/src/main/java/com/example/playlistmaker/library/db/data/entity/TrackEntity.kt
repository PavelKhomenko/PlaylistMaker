package com.example.playlistmaker.library.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "track_table_favorites")
data class TrackEntity (
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