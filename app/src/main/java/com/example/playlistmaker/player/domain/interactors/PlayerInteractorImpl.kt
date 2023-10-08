package com.example.playlistmaker.player.domain.interactors

import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.data.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    override fun getPlayerState(): PlayerState = playerRepository.playerState

    override fun prepare() = playerRepository.preparePlayer()

    override fun start() = playerRepository.startPlayer()

    override fun pause() = playerRepository.pausePlayer()

    override fun release() = playerRepository.stopPlayer()

    override fun getCurrentPosition(): Int = playerRepository.getCurrentPosition()
}