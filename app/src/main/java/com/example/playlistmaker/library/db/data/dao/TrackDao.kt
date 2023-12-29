package com.example.playlistmaker.library.db.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.db.data.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(tracks: TrackEntity)

    @Delete
    suspend fun deleteTrack(tracks: TrackEntity)

    @Query("SELECT * FROM track_table_favorites")
    fun getTrack(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table_favorites")
    suspend fun getTrackId(): List<String>
}