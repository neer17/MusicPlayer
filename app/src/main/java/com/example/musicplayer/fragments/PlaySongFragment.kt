package com.example.musicplayer.fragments


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.PlaySong
import com.example.musicplayer.R
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

/**
 * A simple [Fragment] subclass.
 */
class PlaySongFragment(
    val player: SimpleExoPlayer?,
    val indexOfSong: Int,
    val imageByteArray: ByteArray?
) : Fragment() {
    private val TAG = PlaySong::class.java.simpleName

    private lateinit var parentContext: Context
    private lateinit var inflatedView: View
    private lateinit var playerView: PlayerView

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        itemSelectedListener = context as? OnItemSelectedListener
        if (itemSelectedListener == null)
            throw ClassCastException("$context must implement OnItemSelectedListener")
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentContext = container!!.context
        inflatedView = inflater.inflate(R.layout.fragment_play_song, container, false)
        return inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerView = inflatedView.findViewById(R.id.playerView)
        playerView.player = player

        /*val sourceOfSong = intent.getStringExtra("SOURCE")
        val titleOfSong = intent.getStringExtra("TITLE")*/

        /** imageOfSongByteArray would be null if the song is fetched online as ExoPlayer gets the image from the source itself
         *  @See ListSongAdapter to see how image is passed*/
//        val imageOfSongByteArray: ByteArray? = intent.getByteArrayExtra("IMAGE")

        if (imageByteArray != null) {
            val imageBitmap = byteArrayToBitmap(imageByteArray)
            appendImageToThePlayer(imageBitmap)
        } else appendImageToThePlayer(null)
    }

    private fun appendImageToThePlayer(imageBitmap: Bitmap?) {
        //  setting up image when song is fetched from internal/external storage
        //  in case no image is found default "record" image is displayed
        if (imageBitmap != null)
            playerView.defaultArtwork = BitmapDrawable(this.resources, imageBitmap)
        else
            playerView.defaultArtwork = BitmapDrawable(
                this.resources, BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.record
                )
            )
    }

    private fun byteArrayToBitmap(imageOfSongByteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageOfSongByteArray, 0, imageOfSongByteArray.size)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

}
