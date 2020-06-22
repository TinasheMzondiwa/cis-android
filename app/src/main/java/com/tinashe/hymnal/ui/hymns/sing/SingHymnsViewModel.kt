package com.tinashe.hymnal.ui.hymns.sing

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SingHymnsViewModel @ViewModelInject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    private val mutableHymnal = MutableLiveData<String>()
    val hymnalTitleLiveData: LiveData<String> = mutableHymnal.asLiveData()

    private val mutableHymnsList: MutableLiveData<List<Hymn>> by lazy {
        MutableLiveData<List<Hymn>>().also {
            viewModelScope.launch {
                repository.getHymns().collectLatest { resource ->
                    mutableViewState.postValue(resource.status)
                    mutableHymnsList.postValue(resource.data?.hymns ?: emptyList())
                    resource.data?.title?.let {
                        mutableHymnal.postValue(it)
                    }
                }
            }
        }
    }
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()
}
