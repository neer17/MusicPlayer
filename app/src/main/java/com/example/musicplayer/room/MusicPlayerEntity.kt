package com.example.musicplayer.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicPlayerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val like: Boolean,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val durationPlayed: Long
)