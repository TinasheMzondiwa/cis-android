package com.tinashe.hymnal.ui.hymns.hymnals

import android.net.ConnectivityManager
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.data.repository.RemoteHymnsRepository
import com.tinashe.hymnal.extensions.arch.asLiveData
import com.tinashe.hymnal.extensions.connectivity.isConnected
import kotlinx.coroutines.launch

class HymnalListViewModel @ViewModelInject constructor(
    private val remoteHymnsRepository: RemoteHymnsRepository,
    private val repository: HymnalRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    private val mutableHymnalList = MutableLiveData<List<Hymnal>>()
    val hymnalListLiveData: LiveData<List<Hymnal>> = mutableHymnalList.asLiveData()

    fun loadData() {
        if (connectivityManager.isConnected) {
            loadRemoteHymnals()
        } else {
            loadLocalHymnals()
        }
    }

    private fun loadLocalHymnals() {
        viewModelScope.launch {
            val resource = repository.getHymnals()
            mutableHymnalList.postValue(resource.data ?: emptyList())
        }
    }

    private fun loadRemoteHymnals() {
        viewModelScope.launch {
            val resource = remoteHymnsRepository.listHymnals()
            if (resource.isSuccessFul) {
                val localHymnals = repository.getHymnals().data ?: emptyList()
                val hymnals = resource.data?.sortedBy { it.title } ?: emptyList()
                mutableHymnalList.postValue(
                    hymnals.map { remote ->
                        Hymnal(
                            remote.key,
                            remote.title,
                            remote.language
                        ).also { hymnals ->
                            hymnals.offline = localHymnals.find { it.code == remote.key } != null
                        }
                    }
                )
            } else {
                loadLocalHymnals()
            }
        }
    }
}
