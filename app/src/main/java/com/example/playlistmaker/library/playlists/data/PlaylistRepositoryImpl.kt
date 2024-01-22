package com.example.playlistmaker.library.playlists.data

import com.example.playlistmaker.library.playlists.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.playlists.data.db.PlaylistDataBase
import com.example.playlistmaker.library.playlists.data.db.TrackForPlaylistDataBase
import com.example.playlistmaker.library.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.library.playlists.domain.api.PlaylistRepository
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDataBase: PlaylistDataBase,
    private val trackForPlaylistDataBase: TrackForPlaylistDataBase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDataBase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun addTrackInPlaylist(track: Track) {
        trackForPlaylistDataBase.trackForPlaylistDao()
            .insertTrackForPlaylists(playlistDbConverter.map(track))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDataBase.playlistDao()
            .updatePlaylist(
                tracks = playlistDbConverter.map(playlist.playlistTracks),
                amount = playlist.playlistSize,
                idPlaylist = playlist.id
            )
    }

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistDataBase.playlistDao().getPlaylists().map {
            convertFromEntity(it)
        }

    private fun convertFromEntity(list: List<PlaylistEntity>): List<Playlist> =
        list.map { playlist -> playlistDbConverter.map(playlist) }

}