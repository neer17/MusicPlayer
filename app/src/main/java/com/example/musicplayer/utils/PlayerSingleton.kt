package com.example.musicplayer.utils

import android.content.Context
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer

object PlayerSingleton {
    @Volatile
    var INSTANCE: SimpleExoPlayer? = null

    fun getInstance(context: Context): SimpleExoPlayer =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildInstance(context).also {
                INSTANCE = it
            }
        }


    private fun buildInstance(context: Context) =
        ExoPlayerFactory.newSimpleInstance(context)
}