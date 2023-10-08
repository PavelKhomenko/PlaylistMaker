package com.example.playlistmaker.player

import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactors.PlayerInteractorImpl
import com.example.playlistmaker.player.presentation.PlayerPresenterImpl
import com.example.playlistmaker.player.presentation.PlayerView
import com.example.playlistmaker.player.presentation.Router

object Creator {
    fun connectPresenter(
        playerView: PlayerView,
        activity: AppCompatActivity
    ): PlayerPresenterImpl {
        val router = Router(activity)
        return PlayerPresenterImpl(
            playerView = playerView,
            playerInteractor = PlayerInteractorImpl(PlayerRepositoryImpl(router.getTrack().previewUrl)),
            router = router
            )
    }
}