package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.player.domain.model.Track

sealed interface SearchState {
    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorMessageResId: Int
    ) : SearchState

    data class Empty(
        val emptyMessageResId: Int
    ) : SearchState
}