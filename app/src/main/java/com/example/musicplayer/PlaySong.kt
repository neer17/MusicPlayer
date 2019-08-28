package com.example.musicplayer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.ExoPlayerFactory


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
        audioSource = makeVideoSource(sourceOfSong)
        initializePlayer()
        player!!.prepare(audioSource)
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

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this)
        playerView.player = player
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

    private fun makeVideoSource(sourceOfSong: String):  ProgressiveMediaSource {
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
