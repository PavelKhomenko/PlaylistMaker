package com.example.playlistmaker.library.playlists.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.library.playlists.data.entity.TrackForPlaylistEntity

@Dao
interface TrackForPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackForPlaylists(track: TrackForPlaylistEntity)
}