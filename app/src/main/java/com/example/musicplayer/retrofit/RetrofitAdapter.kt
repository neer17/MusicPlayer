package com.example.musicplayer.retrofit

import com.example.musicplayer.data.AlbumImage
import com.example.musicplayer.data.Music
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val gson: Gson = GsonBuilder()
    .setLenient()
    .create()
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.napster.com/v2.2/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

fun listOfSongs(): Call<Music> {
    return retrofitService.listSongs("MDZmNTA0YmItNDJlNy00YzQxLTkyNDQtZTBhNWNjZTE0YjRh")
}

fun albumImage(albumId: String): Call<AlbumImage> {
    return retrofitService.albumImages(albumId, "MDZmNTA0YmItNDJlNy00YzQxLTkyNDQtZTBhNWNjZTE0YjRh")
}
