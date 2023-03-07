package com.tinashe.hymnal.ui.hymns.sing.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import hymnal.content.api.HymnalRepository
import hymnal.content.model.Hymn
import javax.inject.Inject

const val EXTRA_HYMN = "arg:hymn"

@HiltViewModel
class EditHymnViewModel @Inject constructor(
    private val repository: HymnalRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mutableViewState = SingleLiveEvent<Status>()
    val statusLiveData: LiveData<Status> = mutableViewState.asLiveData()

    private val mutableEditContent = MutableLiveData<Pair<String, Boolean>>()
    val editContentLiveData: LiveData<Pair<String, Boolean>> = mutableEditContent.asLiveData()

    private var editHymn: Hymn? = null

    init {
        savedStateHandle.get<Hymn?>(EXTRA_HYMN)?.let { hymn ->
            editHymn = hymn

            val content = if (hymn.editedContent.isNullOrEmpty()) {
                hymn.content to false
            } else {
                (hymn.editedContent ?: "") to true
            }
            mutableEditContent.postValue(content)
        }
    }

    fun saveContent(content: String) {
        if (content.isNotEmpty()) {
            editHymn?.let { hymn ->

                // remove extra break lines added by aztec
                val suffix = "<br>"
                var parsed = content
                while (parsed.endsWith(suffix)) {
                    parsed = parsed.dropLast(suffix.length)
                }

                hymn.editedContent = parsed
                repository.updateHymn(hymn)

                mutableViewState.postValue(Status.SUCCESS)
            }
        } else {
            mutableViewState.postValue(Status.ERROR)
        }
    }

    fun undoChanges() {
        editHymn?.let { hymn ->
            hymn.editedContent = null
            repository.updateHymn(hymn)

            mutableViewState.postValue(Status.SUCCESS)
        }
    }
}
