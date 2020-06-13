package com.tinashe.christInSong.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CollectionsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is collections Fragment"
    }
    val text: LiveData<String> = _text
}