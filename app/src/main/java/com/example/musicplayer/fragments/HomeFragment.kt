package com.example.musicplayer.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.SongsList
import com.example.musicplayer.retrofit.albumImage
import com.example.musicplayer.retrofit.listOfSongs
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {
    private val TAG = HomeFragment::class.java.simpleName

    private lateinit var networkJob: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        networkJob = Job()

        playlist_1.setOnClickListener {
            //  making a network request to get songs
            CoroutineScope(IO + networkJob).launch {
                //  getting the "previewUrl" and "albumId"
                val call = listOfSongs().execute()
                val songs = call.body()!!.songs
                val albumId = songs[0].albumId
                Log.d(TAG, "onActivityCreated: songs ==> $songs")

                //  getting the album image
                val imageCall = albumImage(albumId).execute()
                val images = imageCall.body()
                Log.d(TAG, "onActivityCreated: images ==> $images")


                val intent = Intent(context, SongsList::class.java)
//                intent.putExtra("SONGS", )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkJob.cancel()
    }
}
