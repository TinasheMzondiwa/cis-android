package com.tinashe.hymnal.ui.hymns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentPickerBinding

class PickHymnFragment : BottomSheetDialogFragment() {

    private var binding: FragmentPickerBinding? = null
    private var hymnCallback: ((Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPickerBinding.bind(view)
        binding?.numberPadView?.setOnNumSelectedCallback {
            hymnCallback?.invoke(it)
            dismiss()
        }
    }

    companion object {
        fun newInstance(callback: (Int) -> Unit): PickHymnFragment = PickHymnFragment().apply {
            hymnCallback = callback
        }
    }
}
