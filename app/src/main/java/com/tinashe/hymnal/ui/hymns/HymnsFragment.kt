package com.tinashe.hymnal.ui.hymns

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tinashe.hymnal.R
import com.tinashe.hymnal.extensions.arch.observeNonNull
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HymnsFragment : Fragment(R.layout.fragment_hymns) {

    private val viewModel: HymnsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.statusLiveData.observeNonNull(viewLifecycleOwner) {
            Timber.i("Status: $it")
        }
        viewModel.hymnListLiveData.observeNonNull(viewLifecycleOwner) {
            Timber.i("Hymns: ${it.size}")
        }
    }
}