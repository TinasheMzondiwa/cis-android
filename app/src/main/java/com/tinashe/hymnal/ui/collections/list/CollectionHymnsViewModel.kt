package com.tinashe.hymnal.ui.collections.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.collections.CollectionHymns
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionHymnsViewModel @Inject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    private val mutableCollection = MutableLiveData<String>()
    val collectionTitleLiveData: LiveData<String> = mutableCollection.asLiveData()

    private val mutableHymnsList = MutableLiveData<List<Hymn>>()
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()

    private var collectionHymns: CollectionHymns? = null
    private var hymnToDelete: Pair<Int, Hymn>? = null

    fun loadData(collectionId: Int) = viewModelScope.launch {
        val resource = repository.getCollection(collectionId)
        mutableViewState.postValue(resource.status)
        resource.data?.let {
            mutableHymnsList.postValue(it.hymns)
            mutableCollection.postValue(it.collection.title)
            collectionHymns = it
        }
    }

    fun onIntentToDeleteHymn(position: Int) {
        val data = mutableHymnsList.value?.toMutableList() ?: return
        hymnToDelete = position to data.removeAt(position)
        mutableHymnsList.postValue(data)
    }

    fun undoDeleteHymn() {
        val data = mutableHymnsList.value?.toMutableList() ?: return
        val pair = hymnToDelete ?: return
        data.add(pair.first, pair.second)
        mutableHymnsList.postValue(data)
        hymnToDelete = null
    }

    fun deleteConfirmed() {
        hymnToDelete?.let { pair ->
            val collection = collectionHymns ?: return@let
            val id = collection.collection.collectionId
            viewModelScope.launch {
                repository.updateHymnCollections(pair.second.hymnId, id, false)
                hymnToDelete = null
            }
        }
    }

    fun deleteCollectionConfirmed() {
        val collection = collectionHymns ?: return
        viewModelScope.launch {
            repository.deleteCollection(collection)
        }
    }
}
