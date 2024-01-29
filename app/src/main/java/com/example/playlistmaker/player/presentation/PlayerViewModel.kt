package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.favorites.domain.FavoritesInteractor
import com.example.playlistmaker.library.playlists.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.library.playlists.presentation.PlaylistState
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    val playerInteractor: PlayerInteractor,
    val favoritesInteractor: FavoritesInteractor,
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    init {
        getData()
    }

    private var timerJob: Job? = null

    private val _playerStatusLiveData = MutableLiveData<PlayerStatus>()
    fun playerStatusLiveData(): LiveData<PlayerStatus> = _playerStatusLiveData

    private val durationLiveData = MutableLiveData<String>()
    fun getDurationLiveData(): LiveData<String> = durationLiveData

    private val favoritesLiveData = MutableLiveData<Boolean>()
    fun getFavoritesLiveData(): LiveData<Boolean> = favoritesLiveData

    private val playlistStateLiveData = MutableLiveData<PlaylistState>()
    fun getPlaylistStateLiveData(): LiveData<PlaylistState> = playlistStateLiveData

    private val isInsidePlaylist = MutableLiveData<Boolean>()
    fun observeInsidePlaylist() = isInsidePlaylist

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun onViewPaused() {
        playerInteractor.pause()
        _playerStatusLiveData.postValue(PlayerStatus.OnPause)
    }

    fun preparePlayer(trackUrl: String) = playerInteractor.prepare(trackUrl)

    private fun startPlayer() {
        playerInteractor.start()
        _playerStatusLiveData.postValue(PlayerStatus.OnStart)

        timerJob = viewModelScope.launch {

            durationLiveData.value = playerInteractor.getCurrentPosition()
            var state = playerInteractor.getPlayerState()

            while (state == PlayerState.STATE_PLAYING) {
                durationLiveData.value = playerInteractor.getCurrentPosition()
                delay(DELAY_MILLIS)
                state = playerInteractor.getPlayerState()
            }

            if (state == PlayerState.STATE_PREPARED) {
                durationLiveData.value = "00:00"
                _playerStatusLiveData.postValue(PlayerStatus.OnPause)
            }
        }
    }

    fun onBtnPlayClicked() {
        when (playerInteractor.getPlayerState()) {
            PlayerState.STATE_PLAYING -> onViewPaused()
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> startPlayer()
            PlayerState.STATE_DEFAULT -> startPlayer()
        }
    }

    fun onBtnFavoritesClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.deleteFromFavorites(track)
                track.isFavorite = false
                favoritesLiveData.value = false
            } else {
                favoritesInteractor.addToFavorites(track)
                track.isFavorite = true
                favoritesLiveData.value = true
            }
        }
    }

    fun isLiked(track: Track) {
        viewModelScope.launch {
            favoritesInteractor.getFavoritesId(track.trackId).collect {
                favoritesLiveData.postValue(it)
                track.isFavorite = it
            }
        }
    }

    private fun getData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlistList: List<Playlist>) {
        if (playlistList.isEmpty()) {
            renderState(PlaylistState.Empty)
        } else {
            renderState(PlaylistState.Content(playlistList))
        }
    }

    private fun renderState(state: PlaylistState) {
        playlistStateLiveData.postValue(state)
    }

    fun isInsidePlaylist(track: Track, playlist: Playlist) {
        isInsidePlaylist.value = playlist.playlistTracks.contains(track.trackId)
    }

    fun updatePlaylist(track: Track, playlist: Playlist) {
        val list = mutableListOf<String>()
        list.addAll(playlist.playlistTracks)
        list.add(track.trackId)
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.updatePlaylist(
                playlist.copy(
                    playlistTracks = list,
                    playlistSize = list.size
                )
            )
        }
    }

    fun addTrackInPlaylist(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.addTrackInPlaylist(track)
        }
    }

    companion object {
        private const val DELAY_MILLIS = 300L
    }
}
