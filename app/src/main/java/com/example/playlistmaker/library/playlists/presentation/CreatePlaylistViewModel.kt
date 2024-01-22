package com.example.playlistmaker.library.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _state = MutableLiveData<Boolean>()
    fun state(): LiveData<Boolean> = _state

    fun hasPlaylistName(status: Boolean) {
        _state.value = status
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
        }
    }
}