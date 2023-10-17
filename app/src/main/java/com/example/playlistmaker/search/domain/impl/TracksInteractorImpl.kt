package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> consumer.consume(resource.data, null)
                is Resource.ServerError -> consumer.consume(null, resource.message)
                is Resource.InternetError -> consumer.consume(null, resource.message)
            }
        }
    }

    override fun getTracksFromSearchHistory(): ArrayList<Track> =
        repository.getTracksFromSearchHistory()


    override fun addTrackToSearchHistory(track: Track) =
        repository.addTrackToSearchHistory(track)


    override fun clearSearchHistory() = repository.clearSearchHistory()

}