package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerInteractor {

    fun getPlayerState(): PlayerState

    fun prepare(trackUrl: String)

    fun start()

    fun pause()

    fun release()

    fun getCurrentPosition(): String
}