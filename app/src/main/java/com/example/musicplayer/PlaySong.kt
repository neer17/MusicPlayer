package com.example.musicplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.data.SongData
import com.example.musicplayer.utils.PlayerSingleton
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerView


class PlaySong : AppCompatActivity() {
    private val TAG = PlaySong::class.java.simpleName

    private var mPlayWhenReady: Boolean = true
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private var positionOfSong: Int = 0

    private lateinit var allSongs: ArrayList<SongData>
    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_song)
        Log.d(TAG, "onCreate: ")

        playerView = findViewById(R.id.playerView)

        /** imageOfSongByteArray would be null if the song is fetched online as ExoPlayer gets the image from the source itself
         *  @See ListSongAdapter to see how image is passed*/
        val imageOfSongByteArray: ByteArray? = intent.getByteArrayExtra("IMAGE")

        //  initializing the player with the image
        if (imageOfSongByteArray != null) {
            val imageBitmap = byteArrayToBitmap(imageOfSongByteArray)
            settingArtworkToThePlayer(imageBitmap)
        } else settingArtworkToThePlayer(null)
    }

    private fun settingArtworkToThePlayer(image: Bitmap?) {
        val player = PlayerSingleton.INSTANCE
        playerView.player = player

        //  setting up image when song is fetched from internal/external storage
        //  in case no image is found default "record" image is displayed
        if (image != null)
            playerView.defaultArtwork = BitmapDrawable(this.resources, image)
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
}
