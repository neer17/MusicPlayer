package com.example.musicplayer.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MusicPlayerDAO {
    @Query("SELECT * FROM MusicPlayerEntity")
    fun getAll(): LiveData<List<MusicPlayerEntity>>

    @Query("SELECT * FROM MusicPlayerEntity WHERE title = (:titleArg)")
    suspend fun getValueFromTitle(titleArg: String): MusicPlayerEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLastPlayedSong(entity: MusicPlayerEntity)

    @Query("DELETE FROM MusicPlayerEntity")
    suspend fun deleteAllSongs()
}

