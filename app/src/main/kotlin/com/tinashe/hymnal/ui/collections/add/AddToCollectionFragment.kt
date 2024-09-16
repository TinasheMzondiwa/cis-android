package com.tinashe.hymnal.ui.collections.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentAddToCollectionBinding
import com.tinashe.hymnal.ui.collections.CollectionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import hymnal.l10n.R as L10nR

@AndroidEntryPoint
class AddToCollectionFragment : BottomSheetDialogFragment() {

    private val viewModel: CollectionsViewModel by viewModels()
    private lateinit var binding: FragmentAddToCollectionBinding

    private val showingAdding: Boolean
        get() = childFragmentManager.findFragmentById(
            R.id.fragment_container
        ) is NewCollectionFragment

    override fun getTheme(): Int = R.style.ThemeOverlay_CIS_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_to_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddToCollectionBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            if (showingAdding) {
                showListFragment()
            } else {
                dismiss()
            }
        }
        binding.btnNew.setOnClickListener {
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
                binding.apply {
                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                    toolbar.setTitle(L10nR.string.new_collection)
                    btnNew.setText(L10nR.string.title_save)
                    btnNew.setIconResource(R.drawable.ic_done)
                }
                showFragment(NewCollectionFragment())
            }
        }

        showListFragment()
    }

    private fun showListFragment() {
        val id = arguments?.getInt(ARG_HYMN_ID) ?: return
        binding.apply {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_down)
            toolbar.setTitle(L10nR.string.add_to_collection)
            btnNew.setText(L10nR.string.title_new)
            btnNew.setIconResource(R.drawable.ic_add)

            showFragment(ListCollectionsFragment.newInstance(id))
        }
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {
        private const val ARG_HYMN_ID = "arg:hymn_id"

        fun newInstance(hymnId: Int): AddToCollectionFragment =
            AddToCollectionFragment().apply {
                arguments = bundleOf(ARG_HYMN_ID to hymnId)
            }
    }
}
