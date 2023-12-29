package com.example.playlistmaker.library.presentation

import com.example.playlistmaker.player.domain.model.Track

sealed interface FavoritesState {

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState

    object Empty : FavoritesState
}