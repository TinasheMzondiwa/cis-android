package com.tinashe.hymnal.ui.hymns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class HymnsViewModel @ViewModelInject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    private val mutableHymnal = MutableLiveData<String>().apply {
        value = "Christ In Song"
    }
    val hymnalTitleLiveData: LiveData<String> = mutableHymnal.asLiveData()

    private val mutableHymnsList: MutableLiveData<List<Hymn>> by lazy {
        MutableLiveData<List<Hymn>>().apply {
            viewModelScope.launch {
                value = repository.hymnsList()
                    .map {
                        mutableViewState.postValue(it.status)
                        it.data ?: emptyList()
                    }.toList(mutableListOf()).flatten()
            }
        }
    }
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()

    fun performSearch(query: String?) {
        viewModelScope.launch {
            val results = repository.searchHymns(query)
            mutableHymnsList.postValue(results)
        }
    }
}