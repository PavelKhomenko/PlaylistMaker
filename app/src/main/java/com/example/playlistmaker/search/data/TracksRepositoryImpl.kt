package com.example.playlistmaker.search.data

import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.sharedPreferences.SearchStorage
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchStorage: SearchStorage,
) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> =  flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> emit(Resource.InternetError())
            200 -> {
                emit(Resource.Success((response as TrackSearchResponse).results))
            }
            else -> emit(Resource.ServerError())
        }
    }

    override fun getTracksFromSearchHistory(): ArrayList<Track> =
        searchStorage.getTracks().toCollection(ArrayList())


    override fun addTrackToSearchHistory(track: Track) = searchStorage.addTrack(track)


    override fun clearSearchHistory() = searchStorage.clearHistory()

}