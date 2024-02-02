package com.example.playlistmaker.library.favorites.data.converters

import com.example.playlistmaker.library.favorites.data.entity.TrackEntity
import com.example.playlistmaker.player.domain.model.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName =  track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country =  track.country,
            trackTime = track.trackTime,
            previewUrl = track.previewUrl
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName =  track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country =  track.country,
            trackTime = track.trackTime,
            previewUrl = track.previewUrl
        )
    }
}