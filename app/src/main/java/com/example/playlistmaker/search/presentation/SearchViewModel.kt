package com.example.playlistmaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private var searchText: String? = null
    private val songsSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        searchRequest(changedText)
    }

    fun searchDebounce(queryInput: String) {
        if (searchText == queryInput) {
            return
        }
        searchText = queryInput
        songsSearchDebounce(queryInput)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, message: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            message == "Internet error" -> {
                searchText = ""
                renderState(
                    SearchState.Error(
                        R.string.connections_issue
                    )
                )
            }

            message == "Server error" -> {
                searchText = ""
                renderState(
                    SearchState.Error(
                        R.string.server_issuses
                    )
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    SearchState.Empty(
                        R.string.nothing_found
                    )
                )
            }

            else -> {
                renderState(
                    SearchState.Content(
                        tracks,
                    )
                )
            }
        }
    }

    fun addTrackToSearchHistory(track: Track) {
        tracksInteractor.addTrackToSearchHistory(track)
    }

    fun getTracksFromSearchHistory(): ArrayList<Track> {
        return tracksInteractor.getTracksFromSearchHistory()
    }

    fun clearSearchHistory() {
        tracksInteractor.clearSearchHistory()
    }


    fun clearSearchText() {
        stateLiveData.postValue(SearchState.HistoryContent(tracksInteractor.getTracksFromSearchHistory()))
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}