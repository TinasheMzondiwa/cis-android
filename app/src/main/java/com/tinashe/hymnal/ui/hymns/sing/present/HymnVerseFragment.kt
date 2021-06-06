package com.tinashe.hymnal.ui.hymns.sing.present

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentHymnVerseBinding
import com.tinashe.hymnal.extensions.view.viewBinding

class HymnVerseFragment : Fragment(R.layout.fragment_hymn_verse) {

    private val binding by viewBinding(FragmentHymnVerseBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val verse = arguments?.getString(ARG_VERSE)
        binding.verseText.text = verse
    }

    companion object {
        private const val ARG_VERSE = "arg:verse"

        fun newInstance(verse: String): HymnVerseFragment = HymnVerseFragment().apply {
            arguments = bundleOf(ARG_VERSE to verse)
        }
    }
}
