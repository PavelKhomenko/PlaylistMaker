package com.example.playlistmaker.player.presentation

sealed class PlayerStatus {
    object OnStart : PlayerStatus()
    object OnPause : PlayerStatus()
}