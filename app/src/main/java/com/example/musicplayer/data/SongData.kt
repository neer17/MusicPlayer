package com.example.musicplayer.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Music(@SerializedName("tracks") val songs: ArrayList<Playlist>)
data class AlbumImage(@SerializedName("images") val images: ArrayList<ImageList>)

data class SongData(val title: String, val artist: String, val image: Any?, val source: String)

@Parcelize
data class Playlist(val id: String, val albumId: String, val previewURL: String) : Parcelable

data class ImageList(val width: Int, val height: Int, val url: String)