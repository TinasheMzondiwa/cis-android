package com.tinashe.hymnal.ui.collections.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.TitleBody
import com.tinashe.hymnal.databinding.FragmentNewCollectionBinding
import hymnal.l10n.R as L10nR

class NewCollectionFragment : Fragment(R.layout.fragment_new_collection) {

    private lateinit var binding: FragmentNewCollectionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewCollectionBinding.bind(view)
    }

    fun getTitleBody(): TitleBody? {
        val title = binding.edtTitle.text?.toString()
        val description = binding.edtDescription.text?.toString()

        return if (title.isNullOrEmpty()) {
            binding.tilTitle.error = getString(L10nR.string.error_required)
            null
        } else {
            TitleBody(title, description)
        }
    }
}
