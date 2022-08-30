package com.tinashe.hymnal.ui.hymns

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.repository.HymnalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HymnsViewModel @Inject constructor(
    private val repository: HymnalRepository,
    private val prefs: HymnalPrefs
) : ViewModel() {

    private val mutableShowHymnalsPrompt = SingleLiveEvent<Any>()
    val showHymnalsPromptLiveData: LiveData<Any> = mutableShowHymnalsPrompt.asLiveData()

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    private val mutableMessage = SingleLiveEvent<String>()
    val messageLiveData: LiveData<String> = mutableMessage.asLiveData()

    private val mutableHymnal = MutableLiveData<String>()
    val hymnalTitleLiveData: LiveData<String> = mutableHymnal.asLiveData()

    private val mutableSelectedHymnId = SingleLiveEvent<Int>()
    val selectedHymnIdLiveData: LiveData<Int> = mutableSelectedHymnId.asLiveData()

    private val mutableHymnsList: MutableLiveData<List<Hymn>> by lazy {
        MutableLiveData<List<Hymn>>().also {
            fetchData()
        }
    }
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()

    fun hymnalSelected(hymnal: Hymnal) {
        fetchData(hymnal)
    }

    fun hymnNumberSelected(context: Context, number: Int) {
        val hymns = mutableHymnsList.value ?: return

        hymns.firstOrNull { it.number == number }?.let {
            mutableSelectedHymnId.postValue(it.hymnId)
        } ?: mutableMessage.postValue(context.getString(R.string.error_invalid_number, number))
    }

    private fun fetchData(hymnal: Hymnal? = null) = viewModelScope.launch {
        repository.getHymns(hymnal).collectLatest { resource ->
            mutableViewState.postValue(resource.status)
            mutableHymnsList.postValue(resource.data?.hymns ?: emptyList())
            resource.data?.title?.let {
                mutableHymnal.postValue(it)

                if (!prefs.isHymnalPromptSeen()) {
                    withContext(Dispatchers.Main) {
                        mutableShowHymnalsPrompt.call()
                    }
                }
            }
        }
    }

    fun performSearch(query: String?) = viewModelScope.launch {
        val results = repository.searchHymns(query)
        mutableHymnsList.postValue(results)
    }

    fun hymnalsPromptShown() {
        prefs.setHymnalPromptSeen()
    }
}
