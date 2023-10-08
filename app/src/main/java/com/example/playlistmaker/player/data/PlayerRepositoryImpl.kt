package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerRepositoryImpl(private val trackUrl: String) : PlayerRepository {
    override var playerState = PlayerState.STATE_DEFAULT

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun preparePlayer() {
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

    override fun stopPlayer() = mediaPlayer.release()

    override fun getCurrentPosition() = mediaPlayer.currentPosition

}