package com.tinashe.hymnal.ui.collections

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentCollectionsBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.AppBarBehaviour
import com.tinashe.hymnal.ui.collections.adapter.CollectionListAdapter
import com.tinashe.hymnal.ui.widget.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsFragment : Fragment() {

    private val viewModel: CollectionsViewModel by viewModels()

    private var appBarBehaviour: AppBarBehaviour? = null

    private var binding: FragmentCollectionsBinding? = null
    private val listAdapter by lazy {
        CollectionListAdapter { pair ->
            val collection = pair.first
            if (collection.hymns.isEmpty()) {
                binding?.snackbar?.show(messageId = R.string.error_empty_collection)
                return@CollectionListAdapter
            }

            val action = CollectionsFragmentDirections
                .actionCollectionHymns(collection.collection.collectionId)
            findNavController().navigate(action)
        }
    }

    private val swipeHandler: ItemTouchHelper.SimpleCallback by lazy {
        object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                viewModel.onIntentToDelete(position)

                binding?.snackbar?.apply {
                    show(
                        messageId = R.string.collection_deleted,
                        actionId = R.string.title_undo,
                        longDuration = true,
                        actionClick = {
                            viewModel.undoDelete()
                            dismiss()
                        },
                        dismissListener = {
                            viewModel.deleteConfirmed()
                        }
                    )
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appBarBehaviour = context as? AppBarBehaviour
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCollectionsBinding.inflate(inflater, container, false).also {
            binding = it
            binding?.listView?.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                val itemTouchHelper = ItemTouchHelper(swipeHandler)
                itemTouchHelper.attachToRecyclerView(this)
                adapter = listAdapter
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewStateLiveData.observeNonNull(viewLifecycleOwner) {
            binding?.apply {
                val size = viewModel.collectionsLiveData.value?.size
                when (it) {
                    ViewState.LOADING -> {
                        listView.isVisible = false
                        emptyView.isVisible = false
                    }
                    ViewState.NO_RESULTS -> {
                        listView.isVisible = false
                        emptyView.isVisible = false
                        progressBar.isVisible = false
                    }
                    ViewState.HAS_RESULTS -> {
                        progressBar.isVisible = false

                        if (size ?: 0 > 0) {
                            emptyView.isVisible = false
                            listView.isVisible = true
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
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.collections_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                appBarBehaviour?.setAppBarExpanded(false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                appBarBehaviour?.setAppBarExpanded(true)
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.performSearch(query)
                return true
            }
        })
    }
}
