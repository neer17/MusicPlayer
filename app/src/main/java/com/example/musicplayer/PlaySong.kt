package com.example.musicplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util.getUserAgent


class PlaySong : AppCompatActivity() {
    private val TAG = PlaySong::class.java.simpleName

    private var mPlayWhenReady: Boolean = true
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0

    private var player: SimpleExoPlayer? = null
    lateinit var playerView: PlayerView
    lateinit var audioSource: ProgressiveMediaSource


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_song)

        playerView = findViewById(R.id.playerView)

        val sourceOfSong = intent.getStringExtra("SOURCE")
        /** imageOfSongByteArray would be null if the song is fetched online as ExoPlayer gets the image from the source itself
         *  @See ListSongAdapter to see how image is passed*/
        val imageOfSongByteArray: ByteArray? = intent.getByteArrayExtra("IMAGE")

        audioSource = makeVideoSource(sourceOfSong)

        if (imageOfSongByteArray != null) {
            val imageBitmap = byteArrayToBitmap(imageOfSongByteArray)
            initializePlayer(imageBitmap)
        } else initializePlayer(null)

        player!!.prepare(audioSource)
    }

    private fun byteArrayToBitmap(imageOfSongByteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageOfSongByteArray, 0, imageOfSongByteArray.size)
    }

    override fun onStart() {
        super.onStart()

        Log.i(
            TAG, "onStart: current windows $currentWindow \n" +
                    " playback position $playbackPosition \n" +
                    " mPlayWhenReady $mPlayWhenReady"
        )

        /* player = ExoPlayerFactory.newSimpleInstance(this)
         player!!.prepare(audioSource)*/
        player!!.let {
            it.playWhenReady = mPlayWhenReady
            it.seekTo(currentWindow, playbackPosition)
        }

    }

    override fun onStop() {
        super.onStop()

        player!!.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.playWhenReady = false
            mPlayWhenReady = false
        }

        Log.i(
            TAG, "onStop: current windows $currentWindow \n" +
                    " playback position $playbackPosition \n"
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        player!!.release()
    }

    private fun initializePlayer(image: Bitmap?) {
        player = ExoPlayerFactory.newSimpleInstance(this)
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


        player!!.let {
            it.playWhenReady = mPlayWhenReady
            it.seekTo(currentWindow, playbackPosition)
        }

        player!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // Active playback.
                    Log.i(TAG, "onPlayerStateChanged: when player is ready")
                    mPlayWhenReady = true

                } else if (playWhenReady) {
                    // Not playing because playback ended, the player is buffering, stopped or
                    // failed. Check playbackState and player.getPlaybackError for details.
                    Log.i(TAG, "onPlayerStateChanged: when playback has stopped")

                } else {
                    // Paused by app.
                    Log.i(TAG, "onPlayerStateChanged: when playback is paused by the app")
                    mPlayWhenReady = false
                }
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.e(TAG, "onPlayerError: ", error)
            }
        })
    }

    private fun makeVideoSource(sourceOfSong: String): ProgressiveMediaSource {
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            getUserAgent(this, "yourApplicationName")
        )
        // This is the MediaSource representing the media to be played.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                Uri.parse(sourceOfSong)
            )
    }
}
