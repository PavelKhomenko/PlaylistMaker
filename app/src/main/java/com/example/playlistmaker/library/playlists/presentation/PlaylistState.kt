package com.example.playlistmaker.library.playlists.presentation

import com.example.playlistmaker.library.playlists.domain.model.Playlist

sealed interface PlaylistState {

    data class Content(val playlistList: List<Playlist>) : PlaylistState

    object Empty : PlaylistState
}