package com.example.playlistmaker.library.playlists.data

import com.example.playlistmaker.library.favorites.data.db.AppDatabase
import com.example.playlistmaker.library.playlists.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.library.playlists.data.entity.TrackForPlaylistEntity
import com.example.playlistmaker.library.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDataBase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDataBase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun addTrackInPlaylist(track: Track) {
        appDataBase.trackForPlaylistDao()
            .insertTrackForPlaylists(playlistDbConverter.map(track))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDataBase.playlistDao()
            .updatePlaylist(
                tracks = playlistDbConverter.map(playlist.playlistTracks),
                amount = playlist.playlistSize,
                idPlaylist = playlist.id
            )
    }

    override fun getPlaylists(): Flow<List<Playlist>> =
        appDataBase.playlistDao().getPlaylists().map {
            convertFromEntity(it)
        }

    override suspend fun deleteCurrentTrackFromPlaylist(
        trackId: String,
        playlistId: Int
    ): Flow<List<Track>> {
        var tempPlaylist: Playlist? = null
        val playlist = playlistDbConverter.map(
            appDataBase.playlistDao().getPlaylistByIdWithoutFlow(playlistId)
        )
        tempPlaylist =
            playlist.copy(playlistTracks = playlist.playlistTracks.filterNot { it.contains(trackId) })
        tempPlaylist =
            tempPlaylist.copy(playlistSize = tempPlaylist.playlistTracks.size)
        appDataBase.playlistDao().updateList(playlistDbConverter.map(tempPlaylist))
        deleteTrackFromPlaylist(trackId, playlistId)
        val flowTracks = appDataBase.trackForPlaylistDao().getTracksFromPlaylist()
            .map { convertFromTracksForPlaylistEntity(it) }
        return flowTracks.map { currentTracks ->
            currentTracks.filter { track ->
                tempPlaylist.playlistTracks.contains(
                    track.trackId
                )
            }
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val trackListFromPlaylist = convertFromTracksForPlaylistEntity(
            appDataBase.trackForPlaylistDao().getTracksFromPlaylistWithoutFlow()
        )
        trackListFromPlaylist.forEach{
            deleteTrackFromPlaylist(it.trackId, playlist.id)
        }
        appDataBase.playlistDao().deletePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun editPlaylist(playlist: Playlist) {
        appDataBase.playlistDao().updateList(playlistDbConverter.map(playlist))
    }

    private fun convertFromEntity(list: List<PlaylistEntity>): List<Playlist> {
        return list.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private fun convertFromTracksForPlaylistEntity(tracks: List<TrackForPlaylistEntity>): List<Track> {
        return tracks.map { track -> playlistDbConverter.map(track) }
    }

    private suspend fun deleteTrackFromPlaylist(trackId: String, playlistId: Int) {
        var toBeDeleted = true
        val tempPlaylistList: List<Playlist> =
            appDataBase.playlistDao().getPlaylistsWithoutFlow().map { playlistDbConverter.map(it) }
        tempPlaylistList.forEach {
            if (it.playlistTracks.contains(trackId) && it.id != playlistId) toBeDeleted = false
        }
        if (toBeDeleted) appDataBase.trackForPlaylistDao().deleteTrackById(trackId)
    }

}