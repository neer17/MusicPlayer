package com.example.musicplayer.fragments


import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import java.io.File


/**
 * A simple [Fragment] subclass.
 *
 */
class SearchFragment : Fragment() {
    private val TAG = SearchFragment::class.java.simpleName

    private lateinit var playerNotificationManager: PlayerNotificationManager
    private val REQUEST_LOCATION: Int = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")

//        //  player notification
//        playerNotificationManager = PlayerNotificationManager(
//            context,
//            DescriptionAdapter(),
//            CHANNEL_ID,
//            NOTIFICATION_ID
//        )

    }
    
    

    private fun getPlayList(rootPath: String): ArrayList<HashMap<String, String>>? {
        val fileList = ArrayList<HashMap<String, String>>()


        try {
            val rootFolder = File(rootPath)
            val files =
                rootFolder.listFiles() //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (file in files) {
                if (file.isDirectory) {
                    if (getPlayList(file.absolutePath) != null) {
                        getPlayList(file.absolutePath)?.let { fileList.addAll(it) }
                    } else {
                        break
                    }
                } else if (file.name.endsWith(".mp3")) {
                    val song = HashMap<String, String>()
                    song["file_path"] = file.absolutePath
                    song["file_name"] = file.name
                    fileList.add(song)
                }
            }
            return fileList
        } catch (e: Exception) {
            return null
        }

    }

    private fun getAllMp3Songs() {
        val arrayList = ArrayList<String>()

        var contentResolver = context!!.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)

            do {
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)
                arrayList.add(currentTitle + "\n" + currentArtist)
            }while(songCursor.moveToNext())
        }

    }


}
