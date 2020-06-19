package com.tinashe.hymnal.ui.hymns.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.repository.RemoteHymnsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class HymnalListViewModel @ViewModelInject constructor(private val repository: RemoteHymnsRepository) :
    ViewModel() {

    fun loadData() {
        viewModelScope.launch {
            val hymns = repository.listHymns()
            Timber.i("HYmns: $hymns")
        }
    }
}