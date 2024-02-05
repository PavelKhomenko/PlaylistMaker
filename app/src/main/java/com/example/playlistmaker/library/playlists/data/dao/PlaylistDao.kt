package com.example.playlistmaker.library.playlists.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.library.playlists.data.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updateList(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("UPDATE playlist_table SET playlistTracks =:tracks, playlistSize =:amount WHERE id == :idPlaylist")
    fun updatePlaylist(tracks: String, amount: Int, idPlaylist: Int)

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    fun getPlaylistById(id: Int): Flow<PlaylistEntity>
}