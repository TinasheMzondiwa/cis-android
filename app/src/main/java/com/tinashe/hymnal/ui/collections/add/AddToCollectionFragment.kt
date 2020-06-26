package com.tinashe.hymnal.ui.collections.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentAddToCollectionBinding
import com.tinashe.hymnal.ui.collections.CollectionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddToCollectionFragment : BottomSheetDialogFragment() {

    private val viewModel: CollectionsViewModel by viewModels()

    private var binding: FragmentAddToCollectionBinding? = null

    private val showingAdding: Boolean
        get() = childFragmentManager.findFragmentById(
            R.id.fragment_container
        ) is NewCollectionFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAddToCollectionBinding.inflate(inflater, container, false).also { it ->
            binding = it
            binding?.apply {
                toolbar.setNavigationOnClickListener {
                    if (showingAdding) {
                        showListFragment()
                    } else {
                        dismiss()
                    }
                }
                btnNew.setOnClickListener {
                    if (showingAdding) {
                        val fragment = childFragmentManager.findFragmentById(
                            R.id.fragment_container
                        )
                        if (fragment is NewCollectionFragment) {
                            fragment.getTitleBody()?.let {
                                viewModel.addCollection(it)
                                showListFragment()
                            }
                        }
                    } else {
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        toolbar.setTitle(R.string.new_collection)
                        btnNew.setText(R.string.title_save)
                        btnNew.setIconResource(R.drawable.ic_done)

                        showFragment(NewCollectionFragment())
                    }
                }
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showListFragment()
    }

    private fun showListFragment() {
        binding?.apply {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_down)
            toolbar.setTitle(R.string.add_to_collection)
            btnNew.setText(R.string.title_new)
            btnNew.setIconResource(R.drawable.ic_add)

            showFragment(ListCollectionsFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
