package com.tinashe.hymnal.ui.hymns

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HymnsViewModel @ViewModelInject constructor(
    private val repository: HymnalRepository,
    private val prefs: HymnalPrefs
) : ViewModel() {

    private val mutableShowHymnalsPrompt = SingleLiveEvent<Any>()
    val showHymnalsPromptLiveData: LiveData<Any> = mutableShowHymnalsPrompt.asLiveData()

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    private val mutableHymnal = MutableLiveData<String>()
    val hymnalTitleLiveData: LiveData<String> = mutableHymnal.asLiveData()

    private val mutableHymnsList: MutableLiveData<List<Hymn>> by lazy {
        MutableLiveData<List<Hymn>>().also {
            fetchData()
        }
    }
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()

    fun hymnalSelected(hymnal: Hymnal) {
        fetchData(hymnal)
    }

    private fun fetchData(hymnal: Hymnal? = null) {
        viewModelScope.launch {
            repository.getHymns(hymnal).collectLatest { resource ->
                mutableViewState.postValue(resource.status)
                mutableHymnsList.postValue(resource.data?.hymns ?: emptyList())
                resource.data?.title?.let {
                    mutableHymnal.postValue(it)

                    if (!prefs.isHymnalPromptSeen()) {
                        prefs.setHymnalPromptSeen()
                        withContext(Dispatchers.Main) {
                            mutableShowHymnalsPrompt.call()
                        }
                    }
                }
            }
        }
    }

    fun performSearch(query: String?) {
        viewModelScope.launch {
            val results = repository.searchHymns(query)
            mutableHymnsList.postValue(results)
        }
    }
}
