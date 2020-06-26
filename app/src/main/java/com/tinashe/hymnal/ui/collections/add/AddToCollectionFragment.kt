package com.tinashe.hymnal.ui.collections.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var adding = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentAddToCollectionBinding.inflate(inflater, container, false).also {
            binding = it
            binding?.apply {
                toolbar.setNavigationOnClickListener {
                    if (adding) {
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_down)
                        toolbar.setTitle(R.string.add_to_collection)
                        btnNew.setText(R.string.title_new)
                        btnNew.setIconResource(R.drawable.ic_add)

                        // switch to list fragment
                    } else {
                        dismiss()
                    }

                    adding = !adding
                }
                btnNew.setOnClickListener {
                    if (adding) {
                        // Save
                    } else {
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                        toolbar.setTitle(R.string.new_collection)
                        btnNew.setText(R.string.title_save)
                        btnNew.setIconResource(R.drawable.ic_done)

                        // switch to add fragment
                    }
                    adding = !adding
                }

            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListCollectionsFragment())
                .commit()
    }
}

