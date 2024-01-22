package com.example.playlistmaker.library.playlists.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.playlists.data.dao.TrackForPlaylistDao
import com.example.playlistmaker.library.playlists.data.entity.TrackForPlaylistEntity

@Database(version = 3, entities = [TrackForPlaylistEntity::class])
abstract class TrackForPlaylistDataBase : RoomDatabase()  {
    abstract fun trackForPlaylistDao(): TrackForPlaylistDao
}