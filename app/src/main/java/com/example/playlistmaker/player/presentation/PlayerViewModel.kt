package com.example.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerViewModel(val playerInteractor: PlayerInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val playerStatusLiveData = MutableLiveData<PlayerStatus>()
    fun getPlayerStatusLiveData(): LiveData<PlayerStatus> = playerStatusLiveData

    private val durationLiveData = MutableLiveData<String>()
    fun getDurationLiveData(): LiveData<String> = durationLiveData

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        handler.removeCallbacksAndMessages(null)
    }

    fun onViewPaused() {
        playerInteractor.pause()
        playerStatusLiveData.postValue(PlayerStatus.OnPause)
        handler.removeCallbacksAndMessages(null)
    }

    fun preparePlayer(trackUrl: String) = playerInteractor.prepare(trackUrl)

    private fun startPlayer() {
        playerInteractor.start()
        playerStatusLiveData.postValue(PlayerStatus.OnStart)
        handler.postDelayed(object : Runnable {
            override fun run() {
                durationLiveData.value = playerInteractor.getCurrentPosition()

                val state = playerInteractor.getPlayerState()
                if (state == PlayerState.STATE_PREPARED) {
                    durationLiveData.value = "00:00"
                    playerStatusLiveData.postValue(PlayerStatus.OnPause)
                    handler.removeCallbacksAndMessages(null)
                }
                handler.postDelayed(this, DELAY)
            }
        }, DELAY)

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
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    playerInteractor = Creator.providePlayerInteractor()
                )
            }
        }
    }
}
