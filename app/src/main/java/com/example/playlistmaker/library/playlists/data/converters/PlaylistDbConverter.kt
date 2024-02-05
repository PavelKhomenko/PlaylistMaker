package com.example.playlistmaker.library.playlists.data.converters

import com.example.playlistmaker.library.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.library.playlists.data.entity.TrackForPlaylistEntity
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity =
        PlaylistEntity(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription ?: "",
            playlistUri = playlist.playlistUri,
            playlistTracks = gson.toJson(playlist.playlistTracks),
            playlistSize = playlist.playlistSize
        )

    fun map(playlist: PlaylistEntity): Playlist {
        val typeToken = object : TypeToken<List<String>>() {}.type
        return Playlist(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistUri = playlist.playlistUri,
            playlistTracks = gson.fromJson(playlist.playlistTracks, typeToken),
            playlistSize = playlist.playlistSize
        )
    }

    fun map(track: Track): TrackForPlaylistEntity =
        TrackForPlaylistEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTime = track.trackTime,
            previewUrl = track.previewUrl
        )

    fun map(track: TrackForPlaylistEntity): Track =
        Track(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTime = track.trackTime,
            previewUrl = track.previewUrl
        )

    fun map(list: List<String>): String = gson.toJson(list)
}