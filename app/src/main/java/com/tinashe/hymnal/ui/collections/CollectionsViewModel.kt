package com.tinashe.hymnal.ui.collections

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.data.model.HymnCollection
import com.tinashe.hymnal.data.model.TitleBody
import com.tinashe.hymnal.data.model.collections.CollectionHymns
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CollectionsViewModel @ViewModelInject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val mutableViewState = SingleLiveEvent<ViewState>()
    val viewStateLiveData: LiveData<ViewState> = mutableViewState.asLiveData()

    private val mutableCollections = MutableLiveData<List<CollectionHymns>>()
    val collectionsLiveData: LiveData<List<CollectionHymns>> = mutableCollections.asLiveData()

    private var collectionToDelete: Pair<Int, CollectionHymns>? = null

    fun loadData() {
        mutableViewState.postValue(ViewState.LOADING)
        viewModelScope.launch {
            repository.getCollectionHymns().collect { collections ->
                mutableCollections.postValue(collections)
                mutableViewState.postValue(ViewState.HAS_RESULTS)
            }
        }
    }

    fun performSearch(query: String?) {
        viewModelScope.launch {
            val collections = repository.searchCollections(query)
            mutableCollections.postValue(collections)
            val state = if (collections.isNotEmpty() || query.isNullOrEmpty()) {
                ViewState.HAS_RESULTS
            } else {
                ViewState.NO_RESULTS
            }
            mutableViewState.postValue(state)
        }
    }

    fun addCollection(content: TitleBody) {
        viewModelScope.launch {
            repository.addCollection(content)
        }
    }

    fun updateHymnCollections(hymnId: Int, collection: HymnCollection, add: Boolean) {
        viewModelScope.launch {
            repository.updateHymnCollections(hymnId, collection.collectionId, add)
        }
    }

    fun onIntentToDelete(position: Int) {
        val data = mutableCollections.value?.toMutableList() ?: return
        collectionToDelete = position to data.removeAt(position)
        mutableCollections.postValue(data)
        mutableViewState.postValue(ViewState.HAS_RESULTS)
    }

    fun undoDelete() {
        val data = mutableCollections.value?.toMutableList() ?: return
        val pair = collectionToDelete ?: return
        data.add(pair.first, pair.second)
        mutableCollections.postValue(data)
        mutableViewState.postValue(ViewState.HAS_RESULTS)
        collectionToDelete = null
    }

    fun deleteConfirmed() {
        collectionToDelete?.let {
            viewModelScope.launch {
                repository.deleteCollection(it.second)
                collectionToDelete = null
            }
        }
    }
}
