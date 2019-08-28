package com.example.musicplayer.data

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class Music(@SerializedName("music") val songs: ArrayList<SongData>)

data class SongData(val title: String, val artist: String, val image: Any?, val source: String)