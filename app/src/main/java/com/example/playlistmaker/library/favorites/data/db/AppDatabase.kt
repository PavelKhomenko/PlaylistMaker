package com.example.playlistmaker.library.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.favorites.data.dao.TrackDao
import com.example.playlistmaker.library.favorites.data.entity.TrackEntity

@Database(version = 2, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}