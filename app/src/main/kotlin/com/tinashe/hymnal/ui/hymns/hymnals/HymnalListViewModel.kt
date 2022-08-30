package com.tinashe.hymnal.ui.hymns.hymnals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tinashe.hymnal.data.model.constants.HymnalSort
import com.tinashe.hymnal.repository.HymnalRepository
import com.tinashe.hymnal.extensions.arch.asLiveData
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HymnalListViewModel @Inject constructor(
    private val repository: HymnalRepository,
    private val prefs: HymnalPrefs
) : ViewModel() {

    private val mutableHymnalList = MutableLiveData<List<HymnalModel>>()
    val hymnalListLiveData: LiveData<List<HymnalModel>> = mutableHymnalList.asLiveData()

    fun loadData() {
        val sorted = sortData(getModels(), prefs.getHymnalSort())
        mutableHymnalList.postValue(sorted)
    }

    fun sortOrderChanged() {
        val data = getModels()
        val sorted = sortData(data, prefs.getHymnalSort())
        mutableHymnalList.postValue(sorted)
    }

    private fun getModels(): List<HymnalModel> {
        val resource = repository.getHymnals()
        return resource.data?.map { hymnal ->
            HymnalModel(
                hymnal.code,
                hymnal.title,
                hymnal.language,
                hymnal.code == prefs.getSelectedHymnal()
            )
        } ?: emptyList()
    }

    private fun sortData(data: List<HymnalModel>, order: HymnalSort): List<HymnalModel> {
        return when (order) {
            HymnalSort.TITLE -> data.sortedBy { it.title }
            HymnalSort.LANGUAGE -> data.sortedBy { it.language }
        }
    }
}
