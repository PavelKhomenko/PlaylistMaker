package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {

    var playerState: PlayerState

    fun preparePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPosition(): Int
}