package com.example.playlistmaker.library.playlistDetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.playlistDetails.domain.api.PlaylistDetailsInteractor
import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistDetailsViewModel(
    val playlistInteractor: PlaylistInteractor,
    val playlistDetailsInteractor: PlaylistDetailsInteractor
) : ViewModel() {
    private val playlistLiveData = MutableLiveData<Playlist>()
    fun getPlaylist(): LiveData<Playlist> = playlistLiveData

    private val trackLiveData = MutableLiveData<List<Track>>()
    fun getTracks(): LiveData<List<Track>> = trackLiveData

    fun getData(playlist: Playlist) {
        viewModelScope.launch {
            playlistDetailsInteractor.getDetailedPlaylist(playlist.id).collect {
                playlistLiveData.value = it
            }
        }
        viewModelScope.launch {
            playlistDetailsInteractor
                .getTracksFromPlaylist(playlist.playlistTracks)
                .collect {
                    trackLiveData.value = it
                }
        }
    }

    fun deleteTrack(trackId: String, playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.deleteCurrentTrackFromPlaylist(trackId, playlistId).collect {
                withContext(Dispatchers.Main) {
                    trackLiveData.value = it
                }
            }
        }
    }
}