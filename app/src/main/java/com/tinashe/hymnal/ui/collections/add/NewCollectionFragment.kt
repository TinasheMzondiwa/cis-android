package com.tinashe.hymnal.ui.collections.add

import androidx.fragment.app.Fragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.TitleBody
import com.tinashe.hymnal.databinding.FragmentNewCollectionBinding
import com.tinashe.hymnal.extensions.view.viewBinding

class NewCollectionFragment : Fragment(R.layout.fragment_new_collection) {

    private val binding by viewBinding(FragmentNewCollectionBinding::bind)

    fun getTitleBody(): TitleBody? {
        val title = binding.edtTitle.text?.toString()
        val description = binding.edtDescription.text?.toString()

        return if (title.isNullOrEmpty()) {
            binding.tilTitle.error = getString(R.string.error_required)
            null
        } else {
            TitleBody(title, description)
        }
    }
}
