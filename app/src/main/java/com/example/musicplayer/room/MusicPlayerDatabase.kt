package com.example.musicplayer.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MusicPlayerEntity::class], version = 1)
abstract class MusicPlayerDatabase : RoomDatabase() {

    abstract fun musicPlayerDao(): MusicPlayerDAO

    companion object {
        @Volatile
        private var INSTANCE: MusicPlayerDatabase? = null

        fun getDatabase(context: Context): MusicPlayerDatabase {
            var tempInstance = INSTANCE
            tempInstance = tempInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicPlayerDatabase::class.java,
                    "music_player_database"
                ).build()
                INSTANCE = instance
                return instance
            }

            return tempInstance
        }
    }
}