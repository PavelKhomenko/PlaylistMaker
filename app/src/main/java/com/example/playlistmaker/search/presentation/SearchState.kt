package com.example.playlistmaker.search.presentation

import androidx.annotation.StringRes
import com.example.playlistmaker.player.domain.model.Track

sealed interface SearchState {
    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class HistoryContent(
        val historyList: List<Track>,
    ) : SearchState

    data class Error(
        @StringRes
        val errorMessageResId: Int
    ) : SearchState

    data class Empty(
        val emptyMessageResId: Int
    ) : SearchState
}