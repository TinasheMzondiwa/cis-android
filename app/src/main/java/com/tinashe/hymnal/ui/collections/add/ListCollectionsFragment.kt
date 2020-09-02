package com.tinashe.hymnal.ui.collections.add

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentCollectionListBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.collections.CollectionsViewModel
import com.tinashe.hymnal.ui.collections.ViewState
import com.tinashe.hymnal.ui.collections.adapter.title.CollectionTitlesListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListCollectionsFragment : Fragment(R.layout.fragment_collection_list) {

    private val viewModel: CollectionsViewModel by viewModels()

    private var binding: FragmentCollectionListBinding? = null

    private val hymnId: Int? get() = arguments?.getInt(ARG_HYMN_Id)

    private val listAdapter = CollectionTitlesListAdapter { pair ->
        hymnId?.let {
            viewModel.updateHymnCollections(it, pair.first.collection, pair.second)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCollectionListBinding.bind(view)
        binding?.listView?.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
        }

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
        viewModel.collectionsLiveData.observeNonNull(viewLifecycleOwner) { models ->
            listAdapter.apply {
                collectionSelectionMap.apply {
                    clear()
                    models.forEach { model ->
                        put(
                            model.collection.collectionId,
                            model.hymns.find { it.hymnId == hymnId } != null
                        )
                    }
                }
                submitList(ArrayList(models))
                notifyDataSetChanged()
            }
        }
        viewModel.loadData()
    }

    companion object {
        private const val ARG_HYMN_Id = "arg:hymn_id"

        fun newInstance(hymnId: Int): ListCollectionsFragment =
            ListCollectionsFragment().apply {
                arguments = bundleOf(ARG_HYMN_Id to hymnId)
            }
    }
}
