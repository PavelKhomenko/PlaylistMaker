package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {

    fun getPlayerState(): PlayerState

    fun preparePlayer(trackUrl: String)

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPosition(): Int
}