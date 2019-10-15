package com.example.musicplayer.room

import androidx.lifecycle.LiveData

class Repository(private val musicPlayerDAO: MusicPlayerDAO) {
    suspend fun getDetailsFromTitle(title: String): MusicPlayerEntity {
        return musicPlayerDAO.getValueFromTitle(title)
    }

    suspend fun insertLastPlayedSong(entity: MusicPlayerEntity) {
        musicPlayerDAO.insertLastPlayedSong(entity)
    }

    suspend fun deleteAllSongs() {
        musicPlayerDAO.deleteAllSongs()
    }

    fun getAllSongs(): LiveData<List<MusicPlayerEntity>> {
        return musicPlayerDAO.getAll()
    }
}