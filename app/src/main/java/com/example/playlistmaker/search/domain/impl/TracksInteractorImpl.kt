package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> Pair(result.data, null)
                is Resource.ServerError -> Pair(null, result.message)
                is Resource.InternetError -> Pair(null, result.message)
            }
        }
    }

    override fun getTracksFromSearchHistory(): ArrayList<Track> =
        repository.getTracksFromSearchHistory()


    override fun addTrackToSearchHistory(track: Track) =
        repository.addTrackToSearchHistory(track)


    override fun clearSearchHistory() = repository.clearSearchHistory()

}