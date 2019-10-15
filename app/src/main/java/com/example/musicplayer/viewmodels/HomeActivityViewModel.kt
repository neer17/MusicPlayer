package com.example.musicplayer.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.room.MusicPlayerDatabase
import com.example.musicplayer.room.MusicPlayerEntity
import com.example.musicplayer.room.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivityViewModel(applicationContext: Application) : ViewModel() {
    private val TAG = HomeActivityViewModel::class.java.simpleName

    private var repository: Repository? = null

    private var _currentWindowIndex = MutableLiveData<Int>()
    val currentWindowIndex: LiveData<Int>
        get() = _currentWindowIndex

    private var __playWhenReady = MutableLiveData<Boolean>()
    val playWhenReady: LiveData<Boolean>
        get() = __playWhenReady

    init {
        val musicPlayerDAO = MusicPlayerDatabase.getDatabase(applicationContext).musicPlayerDao()
        repository = Repository(musicPlayerDAO)

        _currentWindowIndex.value = 0
        __playWhenReady.value = false
    }

    fun insertSong(entity: MusicPlayerEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository!!.deleteAllSongs()
        repository!!.insertLastPlayedSong(entity)
    }

    fun getAllSongs(): LiveData<List<MusicPlayerEntity>> {
        return repository!!.getAllSongs()
    }

    fun changeWindowIndex(index: Int) {
        _currentWindowIndex.value = index
    }

    fun playWhenReady(isPlaying: Boolean) {
        __playWhenReady.value = isPlaying
    }
}