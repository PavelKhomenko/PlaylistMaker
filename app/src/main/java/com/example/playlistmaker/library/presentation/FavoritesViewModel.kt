package com.example.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.db.domain.FavoritesInteractor
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    init {
        getData()
    }

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState() : LiveData<FavoritesState> = stateLiveData

    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }

    private fun processResult(favorites: List<Track>){
        if (favorites.isEmpty()) {
            renderState(FavoritesState.Empty)
        }
        else {
            renderState(FavoritesState.Content(favorites))
        }
    }

    private fun getData() {
        viewModelScope.launch {
            favoritesInteractor
                .getFavoritesTracks()
                .collect{favorites ->
                    processResult(favorites.reversed())
                }
        }
    }
}