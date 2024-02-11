package com.example.playlistmaker.library.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(playlistInteractor: PlaylistInteractor) : CreatePlaylistViewModel(playlistInteractor) {

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun getplaylistLiveData(): LiveData<Playlist> = playlistLiveData

    fun getData(playlist: Playlist) {
        playlistLiveData.value = playlist
    }

    fun editPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.editPlaylist(playlist)
        }
    }
}