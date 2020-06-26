package com.tinashe.hymnal.ui.collections.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.tinashe.hymnal.databinding.FragmentCollectionListBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.collections.CollectionsViewModel
import com.tinashe.hymnal.ui.collections.ViewState
import com.tinashe.hymnal.ui.collections.adapter.CollectionListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListCollectionsFragment : Fragment() {

    private val viewModel: CollectionsViewModel by viewModels()

    private var binding: FragmentCollectionListBinding? = null

    private val listAdapter = CollectionListAdapter { }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCollectionListBinding.inflate(inflater, container, false).also {
            binding = it
            binding?.listView?.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = listAdapter
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewStateLiveData.observeNonNull(viewLifecycleOwner) {
            binding?.apply {
                when (it) {
                    ViewState.LOADING -> {
                        listView.isVisible = false
                        emptyView.isVisible = false
                        progressBar.isVisible = true
                    }
                    ViewState.NO_RESULTS -> {
                        listView.isVisible = false
                        emptyView.isVisible = false
                        progressBar.isVisible = false
                    }
                    ViewState.HAS_RESULTS -> {
                        progressBar.isVisible = false
                        if (listAdapter.itemCount > 0) {
                            listView.isVisible = true
                            emptyView.isVisible = false
                        } else {
                            emptyView.isVisible = true
                            listView.isVisible = false
                        }
                    }
                }
            }
        }
        viewModel.collectionsLiveData.observeNonNull(viewLifecycleOwner) {
            listAdapter.submitList(ArrayList(it))
        }
        viewModel.loadData()
    }
}
