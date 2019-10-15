package com.example.musicplayer.data

import android.os.Parcelable
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class TransferablePlayer(val player: @RawValue SimpleExoPlayer) : Parcelable
