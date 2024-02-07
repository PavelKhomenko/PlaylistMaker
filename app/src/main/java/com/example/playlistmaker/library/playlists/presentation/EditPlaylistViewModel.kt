package com.example.playlistmaker.library.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun getplaylistLiveData(): LiveData<Playlist> = playlistLiveData
    private val buttonStatusLiveData = MutableLiveData<Boolean>()
    fun getButtonLiveData(): LiveData<Boolean> = buttonStatusLiveData

    fun hasPlaylistName(status: Boolean) {
        buttonStatusLiveData.value = status
    }

    fun getData(playlist: Playlist) {
        playlistLiveData.value = playlist
    }

    fun editPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.editPlaylist(playlist)
        }
    }
}