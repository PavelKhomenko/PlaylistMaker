package com.example.playlistmaker.search.data

import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.sharedPreferences.SearchStorage
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.search.domain.api.TracksRepository

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchStorage: SearchStorage,
) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> Resource.InternetError()
            200 -> {
                Resource.Success((response as TrackSearchResponse).results)
            }

            else -> Resource.ServerError()
        }
    }

    override fun getTracksFromSearchHistory(): ArrayList<Track> =
        searchStorage.getTracks().toCollection(ArrayList())


    override fun addTrackToSearchHistory(track: Track) = searchStorage.addTrack(track)


    override fun clearSearchHistory() = searchStorage.clearHistory()

}