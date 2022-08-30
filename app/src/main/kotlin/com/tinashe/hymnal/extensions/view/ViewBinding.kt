package com.tinashe.hymnal.extensions.view

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater(layoutInflater)
    }
}
