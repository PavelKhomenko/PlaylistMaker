package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    override fun getPlayerState(): PlayerState = playerRepository.getPlayerState()

    override fun prepare(trackUrl: String) = playerRepository.preparePlayer(trackUrl)

    override fun start() = playerRepository.startPlayer()

    override fun pause() = playerRepository.pausePlayer()

    override fun release() = playerRepository.stopPlayer()

    override fun getCurrentPosition(): String =
        convertMillisToString(playerRepository.getCurrentPosition())

    private fun convertMillisToString(duration: Int): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
}