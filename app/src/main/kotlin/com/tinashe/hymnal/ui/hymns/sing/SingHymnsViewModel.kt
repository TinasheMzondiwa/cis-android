package com.tinashe.hymnal.ui.hymns.sing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.ui.hymns.HymnsState
import dagger.hilt.android.lifecycle.HiltViewModel
import hymnal.content.api.HymnalRepository
import hymnal.content.model.Hymn
import hymnal.content.model.Hymnal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingHymnsViewModel @Inject constructor(
    private val repository: HymnalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HymnsState>(HymnsState.Loading)
    val uiState: StateFlow<HymnsState> = _uiState

    fun loadData(collectionId: Int) = viewModelScope.launch {
        if (collectionId == -1) {
            repository.getHymns().collectLatest { resource ->
                resource.getOrNull()?.let { hymns ->
                    _uiState.update { HymnsState.Success(hymns.title, hymns.hymns) }
                }
            }
        } else {
            val resource = repository.getCollection(collectionId)
            resource.getOrNull()?.let { hymns ->
                _uiState.update { HymnsState.Success(hymns.collection.title, hymns.hymns) }
            }
        }
    }

    fun switchHymnal(hymnal: Hymnal) {
        repository.setSelectedHymnal(hymnal)
    }

    fun getHymn(position: Int) : Hymn? {
        return (uiState.value as? HymnsState.Success)?.hymns?.getOrNull(position)
    }
}
