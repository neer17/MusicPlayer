package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.adapters.ListSongsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SongsList : AppCompatActivity() {
    private val TAG = SongsList::class.java.simpleName

    lateinit var adapter: ListSongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSongList)

        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)

        /* scope.launch {
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
         }*/
    }
}
