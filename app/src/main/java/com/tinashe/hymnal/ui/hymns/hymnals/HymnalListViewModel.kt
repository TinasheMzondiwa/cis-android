package com.tinashe.hymnal.ui.hymns.hymnals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.constants.HymnalSort
import com.tinashe.hymnal.data.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.asLiveData
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HymnalListViewModel @Inject constructor(
    private val repository: HymnalRepository,
    private val prefs: HymnalPrefs
) : ViewModel() {

    private val mutableHymnalList = MutableLiveData<List<Hymnal>>()
    val hymnalListLiveData: LiveData<List<Hymnal>> = mutableHymnalList.asLiveData()

    fun loadData() {
        val resource = repository.getHymnals()
        resource.data?.forEach {
            it.selected = it.code == prefs.getSelectedHymnal()
        }
        val sorted = sortData(resource.data ?: emptyList(), prefs.getHymnalSort())
        mutableHymnalList.postValue(sorted)
    }

    fun sortOrderChanged() {
        val data = mutableHymnalList.value ?: return
        val sorted = sortData(data, prefs.getHymnalSort())
        mutableHymnalList.postValue(sorted)
    }

    private fun sortData(data: List<Hymnal>, order: HymnalSort): List<Hymnal> {
        return when (order) {
            HymnalSort.TITLE -> data.sortedBy { it.title }
            HymnalSort.LANGUAGE -> data.sortedBy { it.language }
        }
    }
}
