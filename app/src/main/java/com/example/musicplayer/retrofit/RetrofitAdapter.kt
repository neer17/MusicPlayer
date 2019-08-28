package com.example.musicplayer.retrofit

import com.example.musicplayer.data.Music
import com.example.musicplayer.data.SongData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

fun listOfSongs(): Call<Music> {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://storage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService = retrofit.create(RetrofitService::class.java)
    return retrofitService.listSongs()
}