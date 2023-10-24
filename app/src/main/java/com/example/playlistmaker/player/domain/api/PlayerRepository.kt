package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {

    var playerState: PlayerState

    fun preparePlayer(trackUrl: String)

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPosition(): Int
}