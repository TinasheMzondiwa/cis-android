package com.tinashe.hymnal.ui.hymns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tinashe.hymnal.data.repository.HymnalRepository
import timber.log.Timber

class HymnsViewModel @ViewModelInject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    fun test() {
        repository.act()

        if (_text.value == null) {
            Timber.i("Posting value...")
            _text.postValue("This is hymns Fragment")
        }
    }
}