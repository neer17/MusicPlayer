package com.example.musicplayer.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.musicplayer.R
import com.example.musicplayer.adapters.ListSongsAdapter
import com.example.musicplayer.data.OfflineSongData
import com.example.musicplayer.data.SongData
import com.example.musicplayer.utils.GetAllSongs
import kotlinx.android.synthetic.main.activity_songs_list.*
import java.lang.NullPointerException

/**
 * A simple [Fragment] subclass.
 *
 */
class LibraryFragment : Fragment() {
    private val TAG = LibraryFragment::class.java.simpleName

    lateinit var rootView: View
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ListSongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_library, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView = rootView.findViewById(R.id.recycler_view_library_fragment)
        //  getting all songs
        /*val songs: ArrayList<OfflineSongData> = GetAllSongs().getAllTracks(context) as ArrayList<OfflineSongData>
        songs.forEach {
            Log.d(TAG, "onCreate: title: ${it.title} artist: ${it.artist} source: ${it.source}")

        }*/

        var offlineSongs: ArrayList<SongData>? = null
        try {
            offlineSongs = GetAllSongs().initLayout(context)
        } catch (e: NullPointerException) {
            Log.e(TAG, "onActivityCreated: GetAllSongs initLayout", e)
        }

        if (offlineSongs != null) adapter = ListSongsAdapter(offlineSongs)

        recyclerView.adapter = adapter
    }
}