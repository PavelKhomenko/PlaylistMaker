package com.example.playlistmaker.player.presentation

interface PlayerPresenter {
    fun onIvBackPressed()
    fun startPlayer()
    fun pausePlayer()
    fun onBtnPlayClicked()
    fun convertMillisToString(duration: Int): String
    fun onViewPaused()
    fun onViewDestroyed()
}