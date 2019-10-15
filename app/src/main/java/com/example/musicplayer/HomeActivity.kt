package com.example.musicplayer

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.musicplayer.data.SongData
import com.example.musicplayer.fragments.HomeFragment
import com.example.musicplayer.fragments.LibraryFragment
import com.example.musicplayer.fragments.SearchFragment
import com.example.musicplayer.room.MusicPlayerEntity
import com.example.musicplayer.utils.GetAllSongs
import com.example.musicplayer.utils.PlayerSingleton
import com.example.musicplayer.view_model_factory.HomeViewModelFactory
import com.example.musicplayer.viewmodels.HomeActivityViewModel
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), LibraryFragment.FragmentToHomeActivityDataTransfer {
    private val TAG = HomeActivity::class.java.simpleName

    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 101

    lateinit var homeFragment: HomeFragment
    lateinit var searchFragment: SearchFragment
    lateinit var libraryFragment: LibraryFragment
    lateinit var active: Fragment

    private var player: SimpleExoPlayer? = null
    private var mPlayWhenReady: Boolean = true
    private var positionOfSong: Int = 0
    lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    private val fragmentManager = supportFragmentManager
    private lateinit var viewModel: HomeActivityViewModel

    private lateinit var getAllSong: GetAllSongs
    private lateinit var allSongs: ArrayList<SongData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate: ")

        //  asking READ_EXTERNAL_STORAGE permission
        askPermission()

        //  initializing the player
        getPlayerInstance()

        //  getting the songs
        getAllSong = GetAllSongs(this)
        allSongs = getAllSong.offlineTracks

        //  view model factory instance
        val homeViewModelFactory = HomeViewModelFactory(application)

        //  making instances of fragments and adding them to the this activity
        homeFragment = HomeFragment()
        searchFragment = SearchFragment()
        libraryFragment = LibraryFragment()
        active = homeFragment

        fragmentManager.beginTransaction().add(R.id.frameLayout, homeFragment, "HomeFragment")
            .commit()
        fragmentManager.beginTransaction().add(R.id.frameLayout, searchFragment, "SearchFragment")
            .hide(searchFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frameLayout, libraryFragment, "LibraryFragment")
            .hide(libraryFragment).commit()

        //  instantiated all the three fragments
        //  and hiding other upon clicking a specific one
        bottomNavigationBarListener()

        //  onClickListener "song name tv"
        song_name_tv_home_activity.setOnClickListener {
            val intent = Intent(this@HomeActivity, PlaySong::class.java)
            intent.putExtra("HOME_ACTIVITY", true)
            startActivity(intent)
        }

        //  onClickListener "play/pause button"
        play_pause_iv_home_activity.setOnClickListener {
            if (mPlayWhenReady) viewModel.playWhenReady(false)
            else viewModel.playWhenReady(true)
        }

        //  attaching view model
        viewModel =
            ViewModelProviders.of(this, homeViewModelFactory).get(HomeActivityViewModel::class.java)
        //  observing "window index" changes
        viewModel.currentWindowIndex.observe(this, Observer {
            song_name_tv_home_activity.text = getAllSong.titleFromIndex(it)
        })

        //  observing "play when ready" changes
        playPauseChangesObserve()

        //  getting the last played song
        viewModel.getAllSongs().observe(this, Observer {
            var title: String? = null
            try {
                title = it[0].title
            } catch (err: Exception) {
                Log.w(
                    TAG,
                    "onCreate: ArrayIndexOutOfBounds exception would be thrown at the starting"
                )
            }
            Log.d(TAG, "onCreate: title fetched ==> ${title ?: "No song exists"}")
        })
    }

    //  observing "playWhenReady" from "HomesActivityViewModel" and changing the play/pause image
    private fun playPauseChangesObserve() {
        viewModel.playWhenReady.observe(this, Observer {
            player!!.playWhenReady = it
            if (it) play_pause_iv_home_activity.setImageDrawable(
                getDrawable(R.drawable.ic_pause_black_24dp)
            )
            else play_pause_iv_home_activity.setImageDrawable(
                getDrawable(R.drawable.ic_play_arrow_black_24dp)
            )
        })
    }

    //  making player notification and listener
    private fun makePlayerNotification() {
        val playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this,
            "Foreground Service channel Id",
            R.string.PLAYER_CHANNEL_NAME,
            101,
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                    val intent = Intent(this@HomeActivity, PlaySong::class.java)
                    return PendingIntent.getActivity(
                        this@HomeActivity,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                override fun getCurrentContentText(player: Player?): String? {
                    return allSongs[player!!.currentWindowIndex].artist
                }

                override fun getCurrentContentTitle(player: Player?): String {
                    return allSongs[player!!.currentWindowIndex].title
                }

                override fun getCurrentLargeIcon(
                    player: Player?,
                    callback: PlayerNotificationManager.BitmapCallback?
                ): Bitmap? {
                    return allSongs[player!!.currentWindowIndex].image as Bitmap
                }
            },
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    Log.d(TAG, "onNotificationCancelled: ")

                    /**
                     * SAVING the last played song after deleting all the songs in the db
                     * @see HomeActivityViewModel
                     */
                    val currentWindowIndex = player!!.currentWindowIndex
                    val currentSong = allSongs[currentWindowIndex]
                    val position = player!!.currentPosition
                    CoroutineScope(Dispatchers.IO).launch {
                        val musicPlayerEntity =
                            MusicPlayerEntity(false, currentSong.title, position)
                        viewModel.insertSong(musicPlayerEntity)
                    }
                }
            }
        )

        playerNotificationManager.setPlayer(player)
    }

    //  CALLBACK from "LibraryFragment"
    override fun concatenatingMediaSource(concatenatingMediaSource: ConcatenatingMediaSource) {
        this.concatenatingMediaSource = concatenatingMediaSource
        makePlayerNotification()
    }

    //  CALLBACK from "LibraryFragment"
    override fun getPositionOfSong(positionOfSong: Int) {
        this.positionOfSong = positionOfSong
        player!!.let {
            it.prepare(concatenatingMediaSource)
            it.playWhenReady = true
            it.seekTo(positionOfSong, 0)
        }

    }

    //  initializing the player
    private fun getPlayerInstance() {
        //  getting instance
        player = PlayerSingleton.getInstance(this)
        getPlayerListener(player as SimpleExoPlayer)
    }

    //  listening to player events
    private fun getPlayerListener(player: SimpleExoPlayer) {
        //  LISTENER
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // Active playback.
                    Log.i(TAG, "onPlayerStateChanged: PLAYER READY ")
                    mPlayWhenReady = true
                    viewModel.playWhenReady(true)
                } else if (playWhenReady) {
                    // Not playing because playback ended, the player is buffering, stopped or
                    // failed. Check playbackState and player.getPlaybackError for details.
                    Log.i(TAG, "onPlayerStateChanged: PLAYER NOT READY ")
                } else {
                    // Paused by app.
                    Log.i(TAG, "onPlayerStateChanged: PLAYBACK PAUSED")
                    mPlayWhenReady = false
                    viewModel.playWhenReady(false)

                    //  changing the pause icon to play
                    play_pause_iv_home_activity.setImageDrawable(
                        getDrawable(R.drawable.ic_play_arrow_black_24dp)
                    )
                }
            }

            override fun onPositionDiscontinuity(reason: Int) {
                super.onPositionDiscontinuity(reason)

                Log.i(TAG, "onPositionDiscontinuity: reason => $reason")

            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.e(TAG, "onPlayerError: ", error)
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {
                super.onTracksChanged(trackGroups, trackSelections)
                Log.i(TAG, "onTracksChanged: ")

                //changing "changeWindowIndex" and "playWhenReady" MutableLiveData in "HomeActivityViewModel"
                viewModel.changeWindowIndex(player.currentWindowIndex)
                viewModel.playWhenReady(true)
            }

            override fun onSeekProcessed() {
                super.onSeekProcessed()
                Log.d(TAG, "onSeekProcessed: current position ==> ${player.currentPosition}")

            }

            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
                Log.i(TAG, "onLoadingChanged: ")
            }
        })
    }

    //  bottom b=navigation bar listener
    private fun bottomNavigationBarListener() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_bottom_navigation -> {
                    fragmentManager.beginTransaction().hide(active).show(homeFragment).commit()
                    active = homeFragment
                    true
                }

                R.id.search_bottom_navigation -> {
                    fragmentManager.beginTransaction().hide(active).show(searchFragment).commit()
                    active = searchFragment
                    true
                }
                R.id.library_bottom_navigation -> {
                    fragmentManager.beginTransaction().hide(active).show(libraryFragment).commit()
                    active = libraryFragment
                    true
                }
                else -> false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.d(TAG, "onRequestPermissionsResult: permission granted")

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Log.d(TAG, "onRequestPermissionsResult: permission denied")

                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun askPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d(TAG, "onActivityCreated: should request permission be shown")

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            Log.d(TAG, "onActivityCreated: permission has already been granted")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")

        player!!.release()
        player = null
    }
}
