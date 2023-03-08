package com.tinashe.hymnal.ui.hymns.sing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import hymnal.content.api.HymnalRepository
import hymnal.content.model.Hymn
import hymnal.content.model.Hymnal
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingHymnsViewModel @Inject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val mutableHymnal = MutableLiveData<String>()
    val hymnalTitleLiveData: LiveData<String> = mutableHymnal.asLiveData()

    private val mutableHymnsList = MutableLiveData<List<Hymn>>()
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()

    fun loadData(collectionId: Int) = viewModelScope.launch {
        if (collectionId == -1) {
            repository.getHymns().collectLatest { resource ->
                resource.getOrNull()?.let {
                    mutableHymnsList.postValue(it.hymns)
                    mutableHymnal.postValue(it.title)
                }
            }
        } else {
            val resource = repository.getCollection(collectionId)
            resource.getOrNull()?.let {
                mutableHymnsList.postValue(it.hymns)
                mutableHymnal.postValue(it.collection.title)
            }
        }
    }

    fun switchHymnal(hymnal: Hymnal) {
        if (mutableHymnal.value == hymnal.title) {
            return
        }
        viewModelScope.launch {
            repository.getHymns(hymnal).collectLatest { resource ->
                resource.getOrNull()?.let {
                    mutableHymnsList.postValue(it.hymns)
                    mutableHymnal.postValue(it.title)
                }
            }
        }
    }
}
