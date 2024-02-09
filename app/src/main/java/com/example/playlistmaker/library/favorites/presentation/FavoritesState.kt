package com.example.playlistmaker.library.favorites.presentation

import com.example.playlistmaker.player.domain.model.Track

sealed interface FavoritesState {

    data class Content(
        val favoriteTracks: List<Track>
    ) : FavoritesState

    object Empty : FavoritesState
}