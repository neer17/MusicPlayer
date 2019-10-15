package com.example.musicplayer.fragments


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.PlaySong
import com.example.musicplayer.R
import com.example.musicplayer.adapters.ListSongsAdapter
import com.example.musicplayer.data.SongData
import com.example.musicplayer.utils.GetAllSongs
import com.example.musicplayer.viewmodels.HomeActivityViewModel
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 *
 */
class LibraryFragment : Fragment() {
    private val TAG = LibraryFragment::class.java.simpleName

    lateinit var parentContext: Context
    lateinit var rootView: View
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ListSongsAdapter
    lateinit var viewGroup: ViewGroup
    lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    lateinit var getAllSongs: GetAllSongs


    interface FragmentToHomeActivityDataTransfer {
        fun getPositionOfSong(positionOfSong: Int)
        fun concatenatingMediaSource(concatenatingMediaSource: ConcatenatingMediaSource)
    }

    var fragmentToHomeActivityDataTransfer: FragmentToHomeActivityDataTransfer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        parentContext = container!!.context
        this.viewGroup = container
        rootView = inflater.inflate(R.layout.fragment_library, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView = rootView.findViewById(R.id.recycler_view_library_fragment)
        concatenatingMediaSource = ConcatenatingMediaSource()

        //  passing concatenatingMediaSource to the "HomeActivity"
        fragmentToHomeActivityDataTransfer!!.concatenatingMediaSource(makeConcatenatingMediaSource())

        var offlineSongs: ArrayList<SongData>? = null
        try {
            getAllSongs = GetAllSongs(activity)
            offlineSongs = getAllSongs.offlineTracks
        } catch (e: NullPointerException) {
            Log.e(TAG, "onActivityCreated: GetAllSongs getOfflineTracks", e)
        }

        //  songClickListener callback
        val songClickListener = getSongClickListener(offlineSongs, getAllSongs)

        //  setting up and adapter and passing it to Recycler View
        if (offlineSongs != null) adapter = ListSongsAdapter(offlineSongs, songClickListener)
        recyclerView.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentToHomeActivityDataTransfer = context as FragmentToHomeActivityDataTransfer
        fragmentToHomeActivityDataTransfer ?: throw Error("IllegalStateException")
    }

    private fun getSongClickListener(
        offlineSongs: ArrayList<SongData>?,
        getAllSongs: GetAllSongs
    ): ListSongsAdapter.SongClickListener {
        return ListSongsAdapter.SongClickListener { songData, position ->
            val image = songData.image
            val source = songData.source
            val title = songData.title

            val intent = Intent(activity, PlaySong::class.java)
            var imageByteArray: ByteArray? = null

            image.let {
                if (it is Bitmap) {
                    //Convert to byte array
                    val stream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    imageByteArray = stream.toByteArray()
                    intent.putExtra("IMAGE", imageByteArray)
                }
            }

            //  passing position of song to "HomeActivity"
            fragmentToHomeActivityDataTransfer!!.getPositionOfSong(position)

            //  changing play when ready" boolean on "HomeActivity" view model
            activity?.let {
                val viewModel = ViewModelProviders.of(it).get(HomeActivityViewModel::class.java)
                viewModel.playWhenReady(true)
            }


            //  PlaySong.kt
            intent.putExtra("SOURCE", source)
            intent.putExtra("TITLE", title)
            intent.putExtra("POSITION", position)
            startActivity(intent)
        }
    }

    private fun makeMediaSource(sourceOfSong: String): ProgressiveMediaSource {
        /***
         * the source would be a URL when the song is fetched from net and a URI when fetched locally.
         */
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(
            activity,
            Util.getUserAgent(activity, "yourApplicationName")
        )
        // This is the MediaSource representing the media to be played.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                Uri.parse(sourceOfSong)
            )
    }

    private fun makeConcatenatingMediaSource(): ConcatenatingMediaSource {
        val getAllSong = GetAllSongs(activity)
        val allSongs = getAllSong.offlineTracks
        allSongs.forEach {
            concatenatingMediaSource.addMediaSource(makeMediaSource(it.source))
        }

        return concatenatingMediaSource
    }
}