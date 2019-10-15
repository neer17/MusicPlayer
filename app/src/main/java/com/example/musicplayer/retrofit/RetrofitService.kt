package com.example.musicplayer.retrofit

import com.example.musicplayer.data.AlbumImage
import com.example.musicplayer.data.Music
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @GET("tracks/top?limit=5")
    fun listSongs(@Query("apikey") apiKey: String): Call<Music>


    @GET("albums/{albumId}/images")
    fun albumImages(@Path("albumId") albumId: String, @Query("apikey") apiKey: String): Call<AlbumImage>
}