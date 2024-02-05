package com.example.playlistmaker.library.playlistDetails.data

import com.example.playlistmaker.library.favorites.data.db.AppDatabase
import com.example.playlistmaker.library.playlistDetails.domain.api.PlaylistDetailsRepository
import com.example.playlistmaker.library.playlists.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.playlists.data.entity.TrackForPlaylistEntity
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistDetailsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistDetailsRepository {
    override fun getDetailedPlaylist(id: Int): Flow<Playlist> {
        return appDatabase.playlistDao().getPlaylistById(id).map {
            playlistDbConverter.map(it)
        }
    }

    override fun getTracksFromPlaylist(tracks: List<String>): Flow<List<Track>> {
        val list = appDatabase.trackForPlaylistDao().getTracksFromPlaylist().map {
            convertFromTracksFromPlaylistEntity(it)
        }
        return list.map { it.filter { tracks.contains(it.trackId) } }
    }

    private fun convertFromTracksFromPlaylistEntity(trackList: List<TrackForPlaylistEntity>): List<Track> {
        return trackList.map { track -> playlistDbConverter.map(track) }
    }
}