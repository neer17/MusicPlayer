package com.example.musicplayer.retrofit

import com.example.musicplayer.data.Music
import com.example.musicplayer.data.SongData
import retrofit2.Call
import retrofit2.http.GET

public interface RetrofitService {
    @GET("uamp/catalog.json")
    fun listSongs(): Call<Music>
}