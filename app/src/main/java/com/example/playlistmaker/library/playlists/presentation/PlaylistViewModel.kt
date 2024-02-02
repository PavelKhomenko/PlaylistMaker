package com.example.playlistmaker.library.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    init {
        getData()
    }

    private val _state = MutableLiveData<PlaylistState>()
    fun state(): LiveData<PlaylistState> = _state

    private fun renderState(state: PlaylistState) = _state.postValue(state)

    private fun processResult(playlistList: List<Playlist>) =
        if (playlistList.isEmpty()) {
            renderState(PlaylistState.Empty)
        } else {
            renderState(PlaylistState.Content(playlistList))
        }

    private fun getData() =
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
}