package com.example.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicplayer.fragments.HomeFragment
import com.example.musicplayer.fragments.LibraryFragment
import com.example.musicplayer.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val TAG = HomeActivity::class.java.simpleName

    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 101

    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val libraryFragment = LibraryFragment()
    private val fragmentManager = supportFragmentManager
    var active: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate: ")

        //  asking READ_EXTERNAL_STORAGE permission
        askPermission()

        fragmentManager.beginTransaction().add(R.id.frameLayout, homeFragment, "1").commit()
        fragmentManager.beginTransaction().add(R.id.frameLayout, searchFragment, "2").hide(searchFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frameLayout, libraryFragment, "3").hide(libraryFragment).commit()


        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
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

}
