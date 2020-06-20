package com.tinashe.hymnal.ui.hymns.hymnals

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.repository.RemoteHymnsRepository
import com.tinashe.hymnal.extensions.arch.asLiveData
import kotlinx.coroutines.launch

class HymnalListViewModel @ViewModelInject constructor(
        private val repository: RemoteHymnsRepository) : ViewModel() {

    private val mutableHymnalList = MutableLiveData<List<Hymnal>>()
    val hymnalListLiveData: LiveData<List<Hymnal>> = mutableHymnalList.asLiveData()

    fun loadData() {
        viewModelScope.launch {
            val resource = repository.listHymns()
            mutableHymnalList.postValue(resource.data?.sortedBy { it.title })
        }
    }
}