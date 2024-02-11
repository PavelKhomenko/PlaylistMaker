package com.example.playlistmaker.library.playlists.domain.model

data class Playlist(
    val id: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val playlistUri: String,
    val playlistTracks: List<String>,
    val playlistSize: Int
) : java.io.Serializable
