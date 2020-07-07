package com.tinashe.hymnal.ui.hymns.hymnals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.databinding.HymnalListBottomSheetFragmentBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.hymns.hymnals.adapter.HymnalsListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HymnalListBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: HymnalListViewModel by activityViewModels()

    private var binding: HymnalListBottomSheetFragmentBinding? = null

    private var hymnalSelected: ((Hymnal) -> Unit)? = null

    private val listAdapter: HymnalsListAdapter = HymnalsListAdapter {
        hymnalSelected?.invoke(it)
        dismiss()
    }

    private val appBarElevation = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val raiseTitleBar = dy > 0 || recyclerView.computeVerticalScrollOffset() != 0
            binding?.toolbar?.isActivated = raiseTitleBar
        }
    }

    override fun getTheme(): Int = R.style.ThemeOverlay_CIS_BottomSheetDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return HymnalListBottomSheetFragmentBinding.inflate(inflater, container, false).also {
            binding = it
            binding?.toolbar?.setNavigationOnClickListener {
                dismiss()
            }
            binding?.hymnalsListView?.apply {
                addOnScrollListener(appBarElevation)
                adapter = listAdapter
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
        fun newInstance(hymnalSelected: (Hymnal) -> Unit): HymnalListBottomSheetFragment =
            HymnalListBottomSheetFragment().apply {
                this.hymnalSelected = hymnalSelected
            }
    }
}
