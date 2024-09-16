package com.tinashe.hymnal.ui.hymns

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import hymnal.android.coroutines.DispatcherProvider
import hymnal.content.api.HymnalRepository
import hymnal.content.model.Hymnal
import hymnal.prefs.HymnalPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import hymnal.l10n.R as L10nR

@HiltViewModel
class HymnsViewModel @Inject constructor(
    private val repository: HymnalRepository,
    private val prefs: HymnalPrefs,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val mutableShowHymnalsPrompt = SingleLiveEvent<Any>()
    val showHymnalsPromptLiveData: LiveData<Any> = mutableShowHymnalsPrompt.asLiveData()

    private val mutableMessage = SingleLiveEvent<String>()
    val messageLiveData: LiveData<String> = mutableMessage.asLiveData()

    private val mutableSelectedHymnId = SingleLiveEvent<Int>()
    val selectedHymnIdLiveData: LiveData<Int> = mutableSelectedHymnId.asLiveData()

    private val searchQuery = MutableStateFlow<String?>(null)

    val uiState = combine(repository.getHymns(), searchQuery) { result, query ->
        if (query.isNullOrEmpty()) {
            result.getOrNull()?.let {
                HymnsState.Success(it.title, it.hymns).also { showPref() }
            } ?: HymnsState.Error
        } else {
            HymnsState.SearchResults(
                query = query,
                results = repository.searchHymns(query).getOrElse { emptyList() }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HymnsState.Loading)

    fun hymnalSelected(hymnal: Hymnal) {
        repository.setSelectedHymnal(hymnal)
    }

    fun hymnNumberSelected(context: Context, number: Int) {
        val hymns = (uiState.value as? HymnsState.Success)?.hymns ?: return

        hymns.firstOrNull { it.number == number }?.let {
            mutableSelectedHymnId.postValue(it.hymnId)
        } ?: mutableMessage.postValue(context.getString(L10nR.string.error_invalid_number, number))
    }

    private fun showPref() = viewModelScope.launch(dispatcherProvider.main) {
        if (!prefs.isHymnalPromptSeen()) {
            mutableShowHymnalsPrompt.call()
        }
    }

    fun performSearch(query: String?) {
        searchQuery.update { query }
    }

    fun hymnalsPromptShown() {
        prefs.setHymnalPromptSeen()
    }
}
