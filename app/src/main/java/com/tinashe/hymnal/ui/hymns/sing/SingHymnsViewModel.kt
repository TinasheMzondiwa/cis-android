package com.tinashe.hymnal.ui.hymns.sing

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.tinashe.hymnal.data.model.Hymn
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingHymnsViewModel @Inject constructor(
    private val repository: HymnalRepository,
    private val firebaseAnalytics: FirebaseAnalytics
) : ViewModel() {

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    private val mutableHymnal = MutableLiveData<String>()
    val hymnalTitleLiveData: LiveData<String> = mutableHymnal.asLiveData()

    private val mutableHymnsList = MutableLiveData<List<Hymn>>()
    val hymnListLiveData: LiveData<List<Hymn>> get() = mutableHymnsList.asLiveData()

    fun loadData(collectionId: Int) = viewModelScope.launch {
        if (collectionId == -1) {
            repository.getHymns().collectLatest { resource ->
                mutableViewState.postValue(resource.status)
                resource.data?.let {
                    mutableHymnsList.postValue(it.hymns)
                    mutableHymnal.postValue(it.title)
                }
            }
        } else {
            val resource = repository.getCollection(collectionId)
            mutableViewState.postValue(resource.status)
            resource.data?.let {
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
                mutableViewState.postValue(resource.status)
                resource.data?.let {
                    mutableHymnsList.postValue(it.hymns)
                    mutableHymnal.postValue(it.title)
                }
            }
        }
    }

    fun hymnViewed(hymn: Hymn) {
        firebaseAnalytics.logEvent(
            "HYMN_VIEW",
            bundleOf(
                "title" to hymn.title,
                "hymnal" to hymn.book
            )
        )
    }
}
