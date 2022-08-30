package com.tinashe.hymnal.extensions.arch

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(
        owner,
        Observer {
            it?.let(observer)
        }
    )
}

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>
