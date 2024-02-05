package com.example.playlistmaker.library.playlists.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.playlists.data.entity.TrackForPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackForPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackForPlaylists(track: TrackForPlaylistEntity)

    @Query("SELECT * FROM track_table_for_playlist")
    fun getTracksFromPlaylist(): Flow<List<TrackForPlaylistEntity>>
}