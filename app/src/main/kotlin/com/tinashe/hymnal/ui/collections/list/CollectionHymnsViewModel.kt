package com.tinashe.hymnal.ui.collections.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import hymnal.content.api.HymnalRepository
import hymnal.content.model.CollectionHymns
import hymnal.content.model.Hymn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionHymnsViewModel @Inject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val mutableCollection = MutableLiveData<String>()
    val collectionTitleLiveData: LiveData<String> = mutableCollection.asLiveData()

    private val mutableHymnsList = MutableLiveData<List<Hymn>>()
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()

    private var collectionHymns: CollectionHymns? = null
    private var hymnToDelete: Pair<Int, Hymn>? = null

    fun loadData(collectionId: Int) = viewModelScope.launch {
        val resource = repository.getCollection(collectionId)
        resource.getOrNull()?.let {
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

            repository.updateHymnCollections(pair.second.hymnId, id, false)
            hymnToDelete = null
        }
    }

    fun deleteCollectionConfirmed() {
        val collection = collectionHymns ?: return
        repository.deleteCollection(collection)
    }
}
