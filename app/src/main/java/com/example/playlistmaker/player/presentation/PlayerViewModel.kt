package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(val playerInteractor: PlayerInteractor) : ViewModel() {

    private var timerJob : Job? = null

    private val playerStatusLiveData = MutableLiveData<PlayerStatus>()
    fun getPlayerStatusLiveData(): LiveData<PlayerStatus> = playerStatusLiveData

    private val durationLiveData = MutableLiveData<String>()
    fun getDurationLiveData(): LiveData<String> = durationLiveData

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun onViewPaused() {
        playerInteractor.pause()
        playerStatusLiveData.postValue(PlayerStatus.OnPause)
    }

    fun preparePlayer(trackUrl: String) = playerInteractor.prepare(trackUrl)

    private fun startPlayer() {
        playerInteractor.start()
        playerStatusLiveData.postValue(PlayerStatus.OnStart)

        timerJob = viewModelScope.launch {

            durationLiveData.value = playerInteractor.getCurrentPosition()
            var state = playerInteractor.getPlayerState()

            while (state == PlayerState.STATE_PLAYING) {
                durationLiveData.value = playerInteractor.getCurrentPosition()
                delay(DELAY)
                state = playerInteractor.getPlayerState()
            }

            if (state == PlayerState.STATE_PREPARED) {
                durationLiveData.value = "00:00"
                playerStatusLiveData.postValue(PlayerStatus.OnPause)
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

    companion object {
        private const val DELAY = 1000L
    }
}
