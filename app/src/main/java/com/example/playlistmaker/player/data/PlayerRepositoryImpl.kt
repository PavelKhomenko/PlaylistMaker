package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerRepositoryImpl : PlayerRepository {

    private var playerState = PlayerState.STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(trackUrl: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setDataSource(trackUrl)
            prepareAsync()
            playerState = PlayerState.STATE_PREPARED
            setOnCompletionListener {
                playerState = PlayerState.STATE_PREPARED
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING

    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun stopPlayer() {
        mediaPlayer.apply {
            stop()
            reset()
            release()
        }
    }

    override fun getPlayerState(): PlayerState = playerState

    override fun getCurrentPosition() = mediaPlayer.currentPosition

}