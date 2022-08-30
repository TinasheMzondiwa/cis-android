package com.tinashe.hymnal.ui.hymns.sing.present

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentHymnVerseBinding

class HymnVerseFragment : Fragment(R.layout.fragment_hymn_verse) {

    private lateinit var binding: FragmentHymnVerseBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHymnVerseBinding.bind(view)

        binding.verseText.text = arguments?.getString(ARG_VERSE)
    }

    companion object {
        private const val ARG_VERSE = "arg:verse"

        fun newInstance(verse: String): HymnVerseFragment = HymnVerseFragment().apply {
            arguments = bundleOf(ARG_VERSE to verse)
        }
    }
}
