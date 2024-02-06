package com.example.playlistmaker.library.playlists.data.dao

import androidx.room.Dao
import androidx.room.Delete
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

    @Query("DELETE FROM track_table_for_playlist WHERE trackId =:trackId")
    suspend fun deleteTrackById(trackId: String)
}