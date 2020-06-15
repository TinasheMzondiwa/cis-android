package com.tinashe.hymnal.ui.hymns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.repository.HymnalRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class HymnsViewModel @ViewModelInject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is hymns Fragment"
    }
    val text: LiveData<String> = _text

    fun loadData() {
        viewModelScope.launch {
            repository.hymnsList().collect {
                Timber.d("Hymns: ${it.size}")
            }
        }
    }
}