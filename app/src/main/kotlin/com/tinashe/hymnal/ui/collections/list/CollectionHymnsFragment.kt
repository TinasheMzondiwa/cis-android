package com.tinashe.hymnal.ui.collections.list

import android.app.ActivityOptions
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.databinding.FragmentHymnsBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.AppBarBehaviour
import com.tinashe.hymnal.ui.hymns.adapter.HymnListAdapter
import com.tinashe.hymnal.ui.hymns.sing.SingHymnsActivity
import com.tinashe.hymnal.ui.widget.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionHymnsFragment : Fragment(R.layout.fragment_hymns), MenuProvider {

    private val viewModel: CollectionHymnsViewModel by viewModels()
    private lateinit var binding: FragmentHymnsBinding
    private var appBarBehaviour: AppBarBehaviour? = null

    private val listAdapter: HymnListAdapter = HymnListAdapter { pair ->
        val collectionId = arguments?.getInt(ARG_COLLECTION_ID) ?: return@HymnListAdapter
        val intent = SingHymnsActivity.singCollectionIntent(
            requireContext(),
            collectionId,
            pair.first.number
        )
        val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            pair.second,
            getString(R.string.transition_shared_element)
        )
        requireActivity().startActivity(intent, options.toBundle())
    }

    private val swipeHandler: ItemTouchHelper.SimpleCallback by lazy {
        object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                viewModel.onIntentToDeleteHymn(position)

                binding.snackbar.apply {
                    show(
                        messageId = R.string.hymn_deleted,
                        actionId = R.string.title_undo,
                        longDuration = true,
                        actionClick = {
                            viewModel.undoDeleteHymn()
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHymnsBinding.bind(view)

        (requireActivity() as MenuHost)
            .addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.hymnsListView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(this)
            adapter = listAdapter
        }

        viewModel.statusLiveData.observeNonNull(viewLifecycleOwner) {
            binding.apply {
                hymnsListView.isVisible = it != Status.LOADING
                progressBar.isVisible = it == Status.LOADING
            }
        }
        viewModel.collectionTitleLiveData.observeNonNull(viewLifecycleOwner) {
            appBarBehaviour?.setAppBarTitle(it)
        }
        viewModel.hymnListLiveData.observeNonNull(viewLifecycleOwner) { hymns ->
            listAdapter.submitList(ArrayList(hymns))
        }
    }

    override fun onResume() {
        super.onResume()
        val collectionId = arguments?.getInt(ARG_COLLECTION_ID) ?: return
        viewModel.loadData(collectionId)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.collection_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                MaterialAlertDialogBuilder(requireContext(), R.style.Theme_CIS_AlertDialog_Warning)
                    .setTitle(R.string.delete_collection)
                    .setMessage(R.string.delete_collection_message)
                    .setPositiveButton(android.R.string.cancel, null)
                    .setNegativeButton(R.string.title_delete) { _, _ ->
                        viewModel.deleteCollectionConfirmed()
                        findNavController().popBackStack()
                    }
                    .show()
                true
            }
            else -> false
        }
    }

    companion object {
        private const val ARG_COLLECTION_ID = "collectionId"
    }
}
