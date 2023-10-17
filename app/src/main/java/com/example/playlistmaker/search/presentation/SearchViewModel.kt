package com.example.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData


    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private var searchText: String? = null


    fun searchDebounce(queryInput: String) {
        if (searchText == queryInput) {
            return
        }

        this.searchText = queryInput
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)
        val searchRunnable = Runnable { searchRequest(queryInput) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_TOKEN,
            postTime,
        )

    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, message: String?) {
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
            })
        }
    }

    fun removeAllCallbacks() {
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)
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

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = this[APPLICATION_KEY]?.let { Creator.provideTracksInteractor(it) }
                SearchViewModel(interactor!!)
            }
        }
    }
}