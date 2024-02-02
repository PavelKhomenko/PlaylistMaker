package com.example.playlistmaker.library.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.favorites.data.dao.TrackDao
import com.example.playlistmaker.library.favorites.data.entity.TrackEntity
import com.example.playlistmaker.library.playlists.data.dao.PlaylistDao
import com.example.playlistmaker.library.playlists.data.dao.TrackForPlaylistDao
import com.example.playlistmaker.library.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.library.playlists.data.entity.TrackForPlaylistEntity

@Database(version = 2, entities = [TrackEntity::class, PlaylistEntity::class, TrackForPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackForPlaylistDao(): TrackForPlaylistDao
}