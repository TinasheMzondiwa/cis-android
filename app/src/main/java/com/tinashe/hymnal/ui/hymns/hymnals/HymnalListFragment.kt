package com.tinashe.hymnal.ui.hymns.hymnals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tinashe.hymnal.databinding.HymnalListFragmentBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.hymns.hymnals.adapter.HymnalsListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HymnalListFragment : Fragment() {

    private val viewModel: HymnalListViewModel by viewModels()

    private var binding: HymnalListFragmentBinding? = null

    private val listAdapter: HymnalsListAdapter = HymnalsListAdapter {
        with(findNavController()) {
            previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(SELECTED_HYMNAL_KEY, it)
            popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return HymnalListFragmentBinding.inflate(inflater, container, false).also {
            binding = it
            binding?.apply {
                hymnalsListView.adapter = listAdapter
            }
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.hymnalListLiveData.observeNonNull(viewLifecycleOwner) {
            binding?.progressBar?.isVisible = false
            listAdapter.hymnals = it
        }

        viewModel.loadData()
    }

    companion object {
        const val SELECTED_HYMNAL_KEY = "arg:hymnal"
    }
}