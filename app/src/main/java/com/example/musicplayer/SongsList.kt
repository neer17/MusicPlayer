package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.adapters.ListSongsAdapter
import com.example.musicplayer.data.SongData
import com.example.musicplayer.retrofit.listOfSongs
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SongsList : AppCompatActivity() {
    private val TAG = SongsList::class.java.simpleName

    lateinit var adapter: ListSongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSongList)

        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)

        scope.launch {
            withContext(Dispatchers.IO) {
                val music = listOfSongs().execute() //  getting list of songs from the internet
                withContext(Dispatchers.Main) {
                    music.apply {
                        val songs = this.body()!!.songs
                        adapter = ListSongsAdapter(songs)
                        recyclerView.adapter = adapter
                    }
                }

            }
        }
    }
}
