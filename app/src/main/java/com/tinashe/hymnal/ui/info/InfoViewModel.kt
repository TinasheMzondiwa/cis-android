package com.tinashe.hymnal.ui.info

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.tinashe.hymnal.extensions.arch.SingleLiveEvent
import com.tinashe.hymnal.extensions.arch.asLiveData
import kotlinx.coroutines.launch

class InfoViewModel @ViewModelInject constructor(
    private val reviewManager: ReviewManager
) : ViewModel() {

    private val mutableReviewInfo = SingleLiveEvent<ReviewInfo?>()
    val reviewInfoLiveData: LiveData<ReviewInfo?> = mutableReviewInfo.asLiveData()

    init {
        viewModelScope.launch {
            val info = reviewManager.requestReview()
            mutableReviewInfo.postValue(info)
        }
    }

    fun viewResumed() {
        if (mutableReviewInfo.value != null) {
            mutableReviewInfo.call()
        }
    }

    fun reviewLaunched() {
        mutableReviewInfo.value = null
    }
}
