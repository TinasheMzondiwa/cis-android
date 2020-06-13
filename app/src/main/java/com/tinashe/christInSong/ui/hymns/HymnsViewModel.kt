package com.tinashe.christInSong.ui.hymns

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HymnsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is hymns Fragment"
    }
    val text: LiveData<String> = _text
}