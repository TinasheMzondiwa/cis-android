package com.tinashe.hymnal.ui.hymns.hymnals

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.HymnalListFragmentBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.extensions.prefs.HymnalPrefs
import com.tinashe.hymnal.ui.hymns.hymnals.adapter.HymnalsListAdapter
import com.tinashe.hymnal.ui.hymns.hymnals.adapter.SortOptionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HymnalListFragment : Fragment(R.layout.hymnal_list_fragment) {

    @Inject
    lateinit var hymnalPrefs: HymnalPrefs

    private val viewModel: HymnalListViewModel by activityViewModels()

    private var binding: HymnalListFragmentBinding? = null

    private val sortOptionsAdapter: SortOptionsAdapter by lazy {
        SortOptionsAdapter(hymnalPrefs) {
            viewModel.sortOrderChanged()
        }
    }
    private val listAdapter: HymnalsListAdapter = HymnalsListAdapter {
        with(findNavController()) {
            previousBackStackEntry
                ?.savedStateHandle
                ?.set(SELECTED_HYMNAL_KEY, it)
            popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HymnalListFragmentBinding.bind(view)
        binding?.hymnalsListView?.adapter = ConcatAdapter(sortOptionsAdapter, listAdapter)

        viewModel.hymnalListLiveData.observeNonNull(viewLifecycleOwner) { hymnals ->
            binding?.progressBar?.isVisible = false
            listAdapter.submitList(ArrayList(hymnals))
            if (hymnals.size > 1) {
                sortOptionsAdapter.init()
            }
        }

        viewModel.loadData()
    }

    companion object {
        const val SELECTED_HYMNAL_KEY = "arg:hymnal"
    }
}
