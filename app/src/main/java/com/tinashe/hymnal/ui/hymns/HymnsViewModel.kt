package com.tinashe.hymnal.ui.hymns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import kotlinx.coroutines.flow.map

class HymnsViewModel @ViewModelInject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    val hymnListLiveData: LiveData<List<Hymn>> = repository.hymnsList()
        .map {
            mutableViewState.postValue(it.status)
            it.data ?: emptyList()
        }
        .asLiveData()
}