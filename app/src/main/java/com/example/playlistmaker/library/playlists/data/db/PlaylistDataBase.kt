package com.example.playlistmaker.library.playlists.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.playlists.data.dao.PlaylistDao
import com.example.playlistmaker.library.playlists.data.entity.PlaylistEntity

@Database(version = 2, entities = [PlaylistEntity::class])
abstract class PlaylistDataBase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}